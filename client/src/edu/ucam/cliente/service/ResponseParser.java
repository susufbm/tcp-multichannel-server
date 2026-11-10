package edu.ucam.cliente.service;

public class ResponseParser {
		private String type;
		private String numero;
		private String code;
		private String message;
		private String ip;
		private String port;
		
		
		
		
	    public ResponseParser(String respuesta) {
	        if (respuesta == null || respuesta.trim().isEmpty()) {
	            throw new IllegalArgumentException("Respuesta del servidor vacía o nula");
	        }

	        String[] chuncks = respuesta.split(" ");

	        if (chuncks.length >= 3) {
	            type = chuncks[0];
	            numero = chuncks[1];
	            code = chuncks[2];

	            if ("PREOK".equals(type) && chuncks.length >= 5) {
	                ip = chuncks[3];
	                port = chuncks[4];
	            } else if (chuncks.length > 3) {
	                // reconstruir el mensaje con el resto
	                StringBuilder sb = new StringBuilder();
	                for (int i = 3; i < chuncks.length; i++) {
	                    if (i > 3) sb.append(" ");
	                    sb.append(chuncks[i]);
	                }
	                message = sb.toString();
	            }
	        }
	    }
		
		
		//DEVUELVE 1 SI ES IGUAL, 0 SI ES FALSO
		public boolean isSuccess() {
			return "OK".equals(type);
		}
		
		public boolean isPREOK() {
			return "PREOK".equals(type);
		}
		
		public String getMessage() {
			return this.message;
		}
		
		public int getPort() {
			return Integer.parseInt(port);
		}
		
		public String getIp() {
			return this.ip;
		}
		
		public int getNumber() {
			return Integer.parseInt(this.numero);
		}
		
		//ME DEVUELVE EL NUMERO DE ENVIO Y EL CODIGO DE RESPUESTA
		public String numerosDeRespuesta()
		{
			return Integer.parseInt(this.numero) + " " + Integer.parseInt(this.code);
		}
}