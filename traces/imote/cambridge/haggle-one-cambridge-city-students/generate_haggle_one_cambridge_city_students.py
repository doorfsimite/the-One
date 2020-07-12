#!/usr/bin/env python3

# Written by Dimitrios-Georgios Akestoridis <akestoridis@gmail.com>

"""
This script converts the CRAWDAD trace cambridge/haggle/imote/content
(v. 2009-05-29) into the StandardEventsReader format for use in the ONE
simulator. The compressed trace file can be downloaded from
<http://crawdad.org/download/cambridge/haggle/imote-traces-cambridge.tar.gz>.
"""

import sys
import argparse
import hashlib
import tarfile
import csv
import collections
import operator


source_filename = "imote-traces-cambridge.tar.gz"
source_md5_hash = "a5e10aef6751da0df267d66f9d3ff2ee"

mapping_filepath = "./new-to-old-ids.tsv"
output_filepath = "./haggle-one-cambridge-city-students.tsv"
expected_md5_hash = "e085be96504de861fa516d98146ee2a2"

min_node_id = 1
max_node_id = 36

external_entries = 0
loop_entries = 0
backwards_entries = 0


StandardEventsReader = collections.namedtuple("StandardEventsReader",
        ["time", "action", "first_node", "second_node", "type"])


def valid_entry(first_node, second_node, contact_up, contact_down):
    global external_entries
    global loop_entries
    global backwards_entries

    if (first_node < min_node_id
            or second_node < min_node_id
            or first_node > max_node_id
            or second_node > max_node_id):
        external_entries += 1
        return False
    elif first_node == second_node:
        loop_entries += 1
        return False
    elif contact_up > contact_down:
        backwards_entries += 1
        return False

    return True


def get_trace_filepath(node_id):
    max_student_id = 36
    max_lr2_id = 40
    max_sr10_id = 52
    max_sr6_id = 54

    if node_id <= max_student_id:
        input_dir = "./imote-traces-cambridge/SR-10mins-Students/"
    elif node_id <= max_lr2_id:
        input_dir = "./imote-traces-cambridge/LR-2mins/"
    elif node_id <= max_sr10_id:
        input_dir = "./imote-traces-cambridge/SR-10mins-FixLocation/"
    elif node_id <= max_sr6_id:
        input_dir = "./imote-traces-cambridge/SR-6mins-FixLocation/"

    return input_dir + str(node_id) + ".dat"


def get_md5_hash(filepath):
    hasher = hashlib.md5()
    chunk_size = 8192

    with open(filepath, "rb") as fp:
        while True:
            chunk = fp.read(chunk_size)
            hasher.update(chunk)
            if len(chunk) == 0:
                break

    return hasher.hexdigest()


def main(input_filepath):
    # Compare the MD5 hashes of the input file and the source file
    input_md5_hash = get_md5_hash(input_filepath)
    if input_md5_hash != source_md5_hash:
        print("\n[WARNING] The input file \"{}\" has a different MD5 hash " \
                "from the source file \"{}\"!"
                .format(input_filepath, source_filename))
        print("  MD5 hash of the input file: {}".format(input_md5_hash))
        print("  MD5 hash of the source file: {}".format(source_md5_hash))

        answer = ""
        while answer not in ("y", "Y", "n", "N"):
            answer = input("Would you like to try to convert the input " \
                    "file anyway? [Y/n] ")
            if answer in ("n", "N"):
                return 1
    else:
        print("[INFO] The input file \"{}\" has the expected MD5 hash!"
                .format(input_filepath))

    # Extract the contents of the input file
    tar = tarfile.open(input_filepath)
    tar.extractall()
    tar.close()
    print("[INFO] Successfully extracted the contents of the input file in " \
            "the current working directory!")

    # Read the trace files line by line
    starting_time = None
    valid_ids = []
    event_list = []
    for first_node in range(min_node_id, (max_node_id+1)):
        with open(get_trace_filepath(first_node), "r") as fp:
            infile = csv.reader(fp, delimiter=" ", lineterminator="\n")
            for row in infile:
                # Retrieve the information of each entry in a trace file
                second_node = int(row[0])
                contact_up = int(row[1])
                contact_down = int(row[2])

                # Process only valid entries
                if not valid_entry(first_node, second_node,
                        contact_up, contact_down):
                    continue

                # Create the connection-up and connection-down events
                connection_up = StandardEventsReader(
                    time=contact_up,
                    action="CONN",
                    first_node=first_node,
                    second_node=second_node,
                    type="up")
                connection_down = StandardEventsReader(
                    time=contact_down,
                    action="CONN",
                    first_node=first_node,
                    second_node=second_node,
                    type="down")

                # Compute the starting time
                if starting_time is None:
                    starting_time = connection_up.time
                elif connection_up.time < starting_time:
                    starting_time = connection_up.time

                # Create a list of valid IDs
                if first_node not in valid_ids:
                    valid_ids.append(first_node)
                if second_node not in valid_ids:
                    valid_ids.append(second_node)

                # Create a list of connection-up and connection-down events
                event_list.extend((connection_up, connection_down))

    # Sort the list of valid IDs
    valid_ids.sort()

    # Write the new and old node IDs in a file as tab-separated values
    with open(mapping_filepath, "w") as fp:
        outfile = csv.writer(fp, delimiter="\t", lineterminator="\n")
        outfile.writerows(enumerate(valid_ids))

    # Change the node IDs and the simulation time of each event
    for i, event in enumerate(event_list):
        event_list[i] = event_list[i]._replace(
                time=(event.time - starting_time),
                first_node=valid_ids.index(event.first_node),
                second_node=valid_ids.index(event.second_node))

    # Sort the events according to their simulation time
    event_list.sort(key=operator.attrgetter("time"))

    # Generate the output file
    with open(output_filepath, "w") as fp:
        outfile = csv.writer(fp, delimiter="\t", lineterminator="\n")
        outfile.writerows(event_list)

    # Display some useful information
    print("[INFO] The timestamp of the earliest valid contact-up time in " \
            "the source dataset was equal to {}.".format(starting_time))
    if external_entries > 0:
        print("[INFO] {} entries of the source dataset were not " \
                "processed, because external nodes were encountered in " \
                "these entries.".format(external_entries))
    if loop_entries > 0:
        print("[INFO] {} entries of the source dataset were not " \
                "processed, because the encountered node had the same ID " \
                "with the scanning node in these entries."
                .format(loop_entries))
    if backwards_entries > 0:
        print("[INFO] {} entries of the source dataset were not " \
                "processed, because the contact-up time was after the " \
                "contact-down time in these entries."
                .format(backwards_entries))
    print("[INFO] The mapping from new to old node IDs has been written in " \
            "the \"{}\" file.".format(mapping_filepath))
    print("[INFO] The following are the main characteristics of the " \
            "derived trace.")
    print("  Number of Nodes: {}".format(len(valid_ids)))
    print("  Number of Contacts: {}".format(len(event_list) // 2))
    print("  Duration: {} seconds, or about {:.2f} days"
            .format(event_list[-1].time, event_list[-1].time / 86400.0))

    # Compare the MD5 hashes of the output file and the expected file
    output_md5_hash = get_md5_hash(output_filepath)
    if output_md5_hash != expected_md5_hash:
        print("\n[WARNING] The MD5 hash of the output file is different " \
                "from the expected one!")
        print("  Calculated MD5 hash: {}".format(output_md5_hash))
        print("  Expected MD5 hash: {}".format(expected_md5_hash))

        return 1
    else:
        print("[INFO] The output file \"{}\" has the expected MD5 hash!"
                .format(output_filepath))

    return 0


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__,
            formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("input_filepath",
            help="the path to the compressed trace file")
    args = parser.parse_args()

    sys.exit(main(args.input_filepath))
