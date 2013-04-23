package stations;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class Calcul {

	private static double[] factTab=null;
	public static double[] calculer_transmission(double data, double debit,boolean optimized,boolean isack){

		double[] toreturn = new double[4];
		double MPDU;
		int time_difs=50;
		int time_sifs=10;
		double time_ack = 0;//56

		double T_PPDU;
		double N_DBPS;
		double N_PAD=0;

		if(!isack)
			MPDU= data+(20*8)+(16*8); //header + data
		else
			MPDU=14*8;
		if((debit==1) || (debit==2) || (debit==5.5) || (debit==11)){

			T_PPDU=144+48+(MPDU/debit);
		}
		else{
			switch((int)debit){
			case 6:N_DBPS=24;break;
			case 9:N_DBPS=36;break;
			case 12:N_DBPS=48;break;
			case 18:N_DBPS=72;break;
			case 24:N_DBPS=96;break;
			case 36:N_DBPS=144;break;
			case 48:N_DBPS=192;break;
			case 54:N_DBPS=216;break;
			default: N_DBPS=0;break;
			}


			double N_sym = Math.ceil((16+MPDU+6)/N_DBPS) ;
			double N_Data = N_sym * N_DBPS;
			N_PAD = N_Data - (16 + data + 6);
			T_PPDU=16+4+((16+6+N_PAD+MPDU)/debit);
		}


		double transmission_time = T_PPDU;
		if(!isack){
			time_ack= calculer_transmission(0, debit, optimized,true)[0];
			transmission_time+=time_sifs+time_difs+time_ack;
		}



		
		toreturn[0]=transmission_time;
		toreturn[1]=data;
		toreturn[2]=T_PPDU;
		toreturn[3]=T_PPDU-data/debit;

		
		

		return toreturn;

	}

	public static double[] retrouver_data(double temps_transmission, double debit){
		double[] toreturn = new double[4];

		int time_difs=50;
		int time_sifs=10;
		double time_ack = 0;//56



		double N_PAD=0;

		time_ack= Calcul.calculer_transmission(0, debit, false,true)[0];


		if((debit==1) || (debit==2) || (debit==5.5) || (debit==11)){
			toreturn[0]=temps_transmission;
			temps_transmission-=time_sifs+time_difs+time_ack;
			toreturn[2]=temps_transmission;

			toreturn[1]=(temps_transmission-144-48)*debit-(20*8)-(16*8);
			toreturn[3]=temps_transmission-toreturn[1]/debit;
		}
		else{
			
			toreturn[0]=temps_transmission;
			temps_transmission-=time_sifs+time_difs+time_ack;
			toreturn[2]=temps_transmission;
			toreturn[1]=((temps_transmission-16-4-6)*debit)-16-6-N_PAD-(20*8)-(16*8);
			toreturn[3]=temps_transmission-toreturn[1]/debit;

		}

		return toreturn;
	}
	private static double prisParmis(int N,int k){
		if(N<k)
			return 0;

		return (factoriel(N)/factoriel(k))/factoriel(N-k);

	}


	private static double factoriel(int i){

		return factTab[i];

	}

	public static void initialized_facTab(int Max_range){
		factTab=new double [Max_range+1];
		factTab[0]=1.0;
		for (int i=1;i<Max_range+1;i++){

			factTab[i]=i*factTab[i-1];
		}

	}
	public static double[] p_collision_54(double p,int N1,int N54,int nb_stations_min){
		double p_c = 0;
		double p_c_54=0;
		double temp=0;

		if(N1+N54>=1){

			for (int i=nb_stations_min;i<=N54+N1;i++){

				temp=Math.pow(p, i)* Math.pow((1-p), N1+N54-i) * prisParmis(N1+N54,i);
				p_c+=temp;
				if (nb_stations_min==2 && N54<2 ){
					p_c_54=0;
				}
				else {
					if(i<=N54)
						p_c_54+=  temp * (factoriel(N54)/factoriel(N54-i))*(factoriel(N54+N1-i)/factoriel(N54+N1));
				}
			}

		}
		else{
			p_c=0;
			p_c_54=0;
		}

		double[] toReturn = new double [2];
		toReturn[0] = p_c;
		toReturn[1] = p_c_54;
		return toReturn;

	}

	
	public static double[] real_cons(int N){

		double[] toReturn=new double [2];
		double p=-1;
		double q=-1;
		toReturn[0]=p;
		toReturn[1]=q;
		String s=null;
		String[] tabs;

		KernelLink ml = null;
		String[] mathLinkArgs = {"-linkmode", "launch", "-linkname", "C:\\Program Files\\Wolfram Research\\Mathematica\\9.0\\mathkernel"};
		try {
			ml = MathLinkFactory.createKernelLink(mathLinkArgs);
		} catch (MathLinkException e) {
			System.out.println("Fatal error opening link: " + e.getMessage());
			return toReturn;
		}

		try {
			// Get rid of the initial InputNamePacket the kernel will send
			// when it is launched.
			ml.discardAnswer();


			ml.evaluate("Solve[1 - (1 - p)^" +Integer.toString(N-1)+ "== q && p == 1/((((16/(1 - 2*q)) - 1)*0.5) + 1) && 0<p<1 && 0<=q<1, {p, q}, Reals]");
			ml.waitForAnswer();
			s=ml.getExpr().toString();
			tabs= s.split(",");
			if(tabs.length>1){

				p=Double.parseDouble((tabs[1].split("]")[0]));
				q=Double.parseDouble((tabs[3].split("]")[0]));

			}

			toReturn[0]=p;
			toReturn[1]=q;

		} catch (MathLinkException e) {
			System.out.println("MathLinkException occurred: " + e.getMessage());
		} finally {
			ml.close();
		}



		return toReturn;

	}
}
