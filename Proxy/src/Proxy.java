import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
public class Proxy extends Thread {
	private static Semaphore semaforo;
	public Integer getCantUsuariosConectados() {
		return cantUsuariosConectados;
	}
	public void reducirUsuarios() {
		this.cantUsuariosConectados--;
	}
	
	private Integer cantUsuariosConectados =0;
	
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Ingresar el puerto por el que se quiere iniciar el proxy");
		Scanner teclado;
		teclado=new Scanner(System.in);
		int puerto=Integer.parseInt(teclado.next());
		teclado.close();
		new Proxy().EjecutarProxy(puerto);
	}
	
	public void EjecutarProxy (int puerto) throws InterruptedException
	{
		ServerSocket servidor=null;
		Socket scCliente;	
		try
		{
			semaforo=new Semaphore(1, true);
			servidor=new ServerSocket(puerto);
			System.out.println("Proxy activado");
			while(true)
			{
				scCliente=servidor.accept();
				this.cantUsuariosConectados++;
				ManejoDeCliente MC=new ManejoDeCliente(scCliente, this, semaforo, "127.0.0.1", 6000);
				MC.start();
			}
		} catch (IOException e) {

			System.out.println("El puerto ya está siendo utilizado por otra aplicacion");
			e.printStackTrace();
		}
	}
}

