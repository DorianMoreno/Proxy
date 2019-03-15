import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class Proxy extends Thread {
	
	public Integer getCantUsuariosConectados() {
		return cantUsuariosConectados;
	}
	public void setCantUsuariosConectados(Integer cantUsuariosConectados) {
		this.cantUsuariosConectados = cantUsuariosConectados;
	}
	private Integer cantUsuariosConectados =0;
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Ingresar el puerto por el que se quiere iniciar el proxy");
		Scanner teclado;
		teclado=new Scanner(System.in);
		int puerto=Integer.parseInt(teclado.next());
		new Proxy().EjecutarProxy(puerto);
	}
	public void EjecutarProxy (int puerto) throws InterruptedException
	{
		ServerSocket servidor=null;
		Socket scServidor=null;
		Socket scCliente;	
		boolean noConectado=true;
		while(noConectado)
		{
			try {
				scServidor=new Socket("10.192.101.44", 6000);
				noConectado=false;
			}
			catch (IOException e) {

				// TODO Auto-generated catch block
				System.out.println("Conectando con el servidor...\n");
				Thread.sleep(1000);
			}
		}
		try
		{
			servidor=new ServerSocket(puerto);
			System.out.println("Proxy activado");
			while(true)
			{
				scCliente=servidor.accept();
				this.cantUsuariosConectados++;
				ManejoDeCliente MC=new ManejoDeCliente(scCliente,scServidor,this);
				MC.start();
			}
		} catch (IOException e) {

			// TODO Auto-generated catch block
			System.out.println("El puerto ya está siendo utilizado por otra aplicacion");
			//e.printStackTrace();

		}


	}
}

