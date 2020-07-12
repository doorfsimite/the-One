haggle-one-cambridge-city-students
==================================
Bluetooth encounters between 36 mobile nodes from the cambridge/haggle/imote/content trace (v. 2009‑05‑29) have been converted into the StandardEventsReader format for use in the ONE simulator.


Description
-----------
The Python script "generate_haggle_one_cambridge_city_students.py" was written to convert the Bluetooth encounters from the cambridge/haggle/imote/content trace (v. 2009‑05‑29) into the StandardEventsReader format.
This script requires as input the path to the "imote-traces-cambridge.tar.gz" file, which is part of the cambridge/haggle dataset (v. 2009‑05‑29), in order to generate a connectivity trace that can be processed by the ONE simulator.

The first step for the derivation of the connectivity trace was to compute the starting time, i.e. the timestamp of the earliest valid encounter, which was equal to 1130493332 (Fri, 28 Oct 2005 09:55:32 GMT).
Only encounters between the 36 Cambridge students were used to generate the connectivity trace.
As a result, 10562 of their entries were not processed, because external nodes were encountered in these entries.
A list of valid IDs was created in order to rename the nodes in the derived trace by enumerating them from zero.
In addition, a mapping from new to old node IDs was written to the "new-to-old-ids.tsv" file.
The simulation time of each connection-up and connection-down event was then calculated, which corresponds to its original timestamp minus the starting time.
The connection events were then sorted according to their simulation time.
Finally, the sorted list of connection events was used to create the "haggle-one-cambridge-city-students.tsv" file, which can be used as input to the ONE simulator.

The main characteristics of the connectivity trace can be summarized as follows.

* Number of Nodes: 36
* Number of Contacts: 10641
* Duration: 987529 seconds, or about 11.43 days


Format of trace data
--------------------
The "haggle-one-cambridge-city-students.tsv" file stores tab-separated values of connection events, in accordance with the syntax of the StandardEventsReader format, which can be used for network simulations with the ONE simulator.
Each line of this file describes a connection event and has the following five fields:

[time] [action] [first_node] [second_node] [type]

The first field corresponds to the simulation time at which the event occurred.
The second field is always equal to "CONN" since all the events in the connectivity trace are either connection-up or connection-down events.
The values of the third and fourth fields correspond to the IDs of two nodes.
The fifth field is either "up" when two nodes connect with each other or "down" when two nodes disconnect with each other.


Contributors
------------
Dimitrios-Georgios Akestoridis <akestoridis@gmail.com>


References
----------
J. Scott, R. Gass, J. Crowcroft, P. Hui, C. Diot, and A. Chaintreau, "CRAWDAD dataset cambridge/haggle (v. 2009‑05‑29)," downloaded from http://crawdad.org/cambridge/haggle/20090529, 2009. DOI: 10.15783/C70011.

A. Keränen, J. Ott, and T. Kärkkäinen. "The ONE Simulator for DTN Protocol Evaluation," in Proceedings of the 2nd International Conference on Simulation Tools and Techniques (SIMUTOOLS), 2009. DOI: 10.4108/ICST.SIMUTOOLS2009.5674.
