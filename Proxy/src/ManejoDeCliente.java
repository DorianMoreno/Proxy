import java.awt.image.SampleModel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;

public class ManejoDeCliente extends Thread{
	DataInputStream in;
	DataOutputStream out;
	private static Semaphore semaforo;
	Proxy proxy;
	private Socket sc;
	//private Socket scServidor;
	ManejoDeCliente(Socket socketDelCliente, Proxy pro,Semaphore sema )
	{
		super();
		this.semaforo=sema;
		sc=socketDelCliente;
		//scServidor=socketDelServidor;
		proxy=pro;
	}


	private void SemaforoWait()
	{
		Boolean b;
		do {
			try {
				semaforo.acquire();
				b = true;
			}catch(Exception e)
			{
				System.out.println(e);
				b=false;
			}
		}while(semaforo.availablePermits() != 0 && b.equals(true));
	}

	public void run()
	{
		Socket scDataBase; 
		String nombreUsuario;
		String hashUsuario;
		String regionUsuario;
		DataInputStream inDB;
		DataOutputStream outDB;

		try {	
			System.out.println("Conexion solicitada de la ip: "+sc.getInetAddress()+" "+sc.getPort());
			//Cliente conectado	
			in= new DataInputStream(sc.getInputStream());
			out= new DataOutputStream(sc.getOutputStream());
			String mensajeDelCliente = " ";
			String mensajeDeLaBD;
			String hash;
			//out.writeUTF("Mensaje del servidor: conexion exitosa");
			//Inicio de sesion del usuario
			boolean usuarioConectado=true;
			while(usuarioConectado) {
				mensajeDelCliente=in.readUTF();

				if(mensajeDelCliente.equals("Registro"))//En el inicio de usuario de uncliente
				{
					regionUsuario=in.readUTF();//Recibe registro del usuario
					hash = in.readUTF();
					this.SemaforoWait();//Comienza seccion critica
					
					scDataBase= new Socket("127.0.0.1",7000);//Conectarse al database
					inDB= new DataInputStream(scDataBase.getInputStream());
					outDB= new DataOutputStream(scDataBase.getOutputStream());
					outDB.writeUTF("Registrar");//Pedirle id al database
					outDB.writeUTF(regionUsuario);// Se envia la region del usuario
					outDB.writeUTF(hash);
					out.writeUTF(inDB.readUTF());	//enviarle el id al usuario


					//

					scDataBase.close();
					semaforo.release();//Termina seccion critica
				}
				if(mensajeDelCliente.equals("1"))
				{
					System.out.println("La conexion de"+sc.getInetAddress()+" "+sc.getPort()+ " pertenece al manager, enviar carga del proxy");
					////
					System.out.println("Cantidad usuarios: "+(proxy.getCantUsuariosConectados()));
					out.writeUTF(String.valueOf(proxy.getCantUsuariosConectados()));
					/////
				}

				
				if(mensajeDelCliente.trim().equals("Salir"))
				{
					sc.close();
					proxy.setCantUsuariosConectados(proxy.getCantUsuariosConectados()-1);
					usuarioConectado=false;
				}
				if(mensajeDelCliente.trim().equals("IniciarSesion"))
				{
					System.out.println("Cliente "+sc.getInetAddress()+" "+sc.getPort() +" esta intentando iniciar sesion");
					nombreUsuario=in.readUTF();//Recibe el nombre de usuario
					hashUsuario = in.readUTF();//Recibe el hash de la contraseña del usuario
					this.SemaforoWait();//Comienza seccion critica
					
					scDataBase= new Socket("127.0.0.1",7000);//Conectarse al database
					inDB= new DataInputStream(scDataBase.getInputStream());
					outDB= new DataOutputStream(scDataBase.getOutputStream());
					outDB.writeUTF("BuscarID");//Decirle al database que va a buscar un id ya existente
					outDB.writeUTF(nombreUsuario);//Pasarle el id del usuario al DB
					outDB.writeUTF(hashUsuario);//Pasarle el hash de la contraseña del usuario al DB
					out.writeUTF(inDB.readUTF());//Espera confirmacion de la DB //Informar al usuario si su id existe o no
					out.writeUTF(inDB.readUTF());//Si el usuario existe manda su territorio, sino envía un "false"
			
					
					scDataBase.close();
					

					semaforo.release();//Termina seccion critica

				}


			}		
			System.out.println("desconectado el cliente"+sc.getInetAddress()+" "+sc.getPort());
		} catch (Exception e) {

			// TODO Auto-generated catch block
			System.out.println("Se cayo la conexion en: "+sc.getInetAddress()+" "+sc.getPort());
			proxy.setCantUsuariosConectados(proxy.getCantUsuariosConectados()-1);
			e.printStackTrace();
		}



	}
}