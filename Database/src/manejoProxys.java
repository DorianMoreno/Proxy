import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;
public class manejoProxys extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket scProxy;
	Semaphore semaforo;
	Database baseDeDatos;
	manejoProxys(Socket socket,Semaphore semafo, Database base) throws IOException
	{
			this.baseDeDatos=base;
			scProxy=socket;
			semaforo=semafo;
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
		try {
			in= new DataInputStream(scProxy.getInputStream());
			out= new DataOutputStream(scProxy.getOutputStream());
			String mensaje;
			String idUsuario;
			String hashUsuario;
			String hash;
			String territorio;
			
			while(true)
			{
				String aux=in.readUTF();
				
				
				if(aux.equals("BuscarID"))
				{
					
					this.SemaforoWait();
					System.out.println("Proxy "+this.scProxy.getInetAddress()+" desea buscar si una id esta registrada");
					idUsuario = in.readUTF();//Lee el id del usuario
					hashUsuario = in.readUTF();//Lee el id del usuario
					if(this.baseDeDatos.buscarSiUsuarioExiste(idUsuario, hashUsuario))//Mandar a la clase DB a buscar el id del usuario en la lista de usuario
					{//Si el id esta registrado
						this.out.writeUTF("true");//Confirmar inicio de sesion exitosa
					}else
					{
					//Si el id no esta registrado
						this.out.writeUTF("false");//Confirmar inicio de sesion no exitosa
					}
					scProxy.close();
					semaforo.release();
					return;
				}
				
				if(aux.equals("Registrar"))
				{
					this.SemaforoWait();//Comienzo de seccion critica
					territorio = in.readUTF();
					hash = in.readUTF();

					out.writeUTF(String.valueOf(this.baseDeDatos.id));
										
					this.baseDeDatos.id++;
					Usuario usu = new Usuario(String.valueOf(this.baseDeDatos.id-1),  hash, territorio);
					this.baseDeDatos.usuarios.add(usu);
					scProxy.close();
					semaforo.release();//Fin de seccion critica
					return;
				}
			}
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
