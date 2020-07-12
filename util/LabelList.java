package util;

public class LabelList {

		int host;
		public String comunidade;
		private String subComunidade;
		private String curso;
		private String turno;
		
	public LabelList(int h,String c,String sc,String cur,String tur){
		this.host = h;
		this.comunidade = c;
		this.subComunidade = sc;
		this.curso = cur;
		this.turno = tur;
	}
	public LabelList() {
		this.host = -1;
		this.comunidade = "";
		this.subComunidade = "";
		this.curso = "";
		this.turno = "";
	}
	
	public String toString() {
		if(this.comunidade.equals("") ||this.subComunidade.equals("") || this.curso.equals("") || this.turno.equals("")) {
			return "Don't have label";
		}
		else {
			return String.valueOf(this.host) + " " +this.comunidade + " " + this.subComunidade + " " + this.curso + " "+ this.turno;
		}
	}
	
}
