package stations;


public class Station {

	public final static int nb_packets_init=Main.nb_packets_limit;
	private int nb_packets=nb_packets_init;
	public final static int packet_voice_length=80*8;
	public final static int packet_data_length=1500*8;
	private int CW=16;
	private double debit;
	private int backoff;
	private int data_remained;
	private double proba_data;
	private double time_to_send_frames_1=0;
	private double time_to_send_frames_54=0;
	public double nb_slots=0;
	public double nb_slots_sending=0;
	public double nb_slots_collisions=0;
	public double cwmean=0;
	private double cw_mean_to_valid=0;
	
	public Station(double debit,double proba_data) {


		this.debit=debit;


		
		if (Math.random()<proba_data){  //tirage aléatoire du nouveau paquet
			
			data_remained=Station.packet_data_length;
		}
		else{
			data_remained=Station.packet_voice_length;
		}
		this.proba_data=proba_data;
		
		backoff= (int)((Math.random()*CW)); //nouveau backoff
		cw_mean_to_valid=backoff;

	}


	public double getTime_to_send_frames_1() {
		return time_to_send_frames_1;
	}



	public double getTime_to_send_frames_54() {
		return time_to_send_frames_54;
	}



	public void ajouter_temps(double time){ //temps de la station dans la simulation
		
		if(debit==54 && nb_packets>0)
			time_to_send_frames_54+=time;
		if(debit==1 && nb_packets>0)
			time_to_send_frames_1+=time;
		

	}

	
	public void reinitialiser(){

		CW=16;
		
		
		if (Math.random()<proba_data){
			data_remained=Station.packet_data_length;
		}
		else{
			data_remained=Station.packet_voice_length;
		}
		
		time_to_send_frames_1=0;
		time_to_send_frames_54=0;
		nb_packets=nb_packets_init;
		cwmean=0;
		nb_slots=0;
		nb_slots_sending=0;
		nb_slots_collisions=0;

		backoff= (int)((Math.random()*CW));
		cw_mean_to_valid=backoff;

	}

	public double getDebit() {
		return debit;
	}



	public int getBackoff() {
		return backoff;
	}

	public void collision(){

		CW*=2;
		if (CW>1024)
			CW=1024;
		nb_slots+=cw_mean_to_valid+1;
		cwmean+=cw_mean_to_valid;
		nb_slots_sending++;
		nb_slots_collisions++;
		backoff= (int)((Math.random()*CW));
		cw_mean_to_valid=backoff;
		
	}

	public void decompter_backoff(int nbackoff){
		backoff-=nbackoff;

	}

	public void new_backoff(){ //après emission avec succes de données => reinitialisation du backoff et tirage d'un nouveau

		CW=16;
		nb_slots+=cw_mean_to_valid+1;
		cwmean+=cw_mean_to_valid;
		nb_slots_sending++;
		backoff= (int)((Math.random()*CW));
		
		cw_mean_to_valid=backoff;
	}

	public void data_remained(int data){


		data_remained=data;

		if (data<=0){ //si paquet fini
			if (Math.random()<proba_data){
				data_remained=packet_data_length;
			}
			else{
				data_remained=packet_voice_length;

			}

			if(Main.data_limited) //si on a mis une limite en nombre de paquets
				nb_packets--;
			
		}
	}


	

	public int getData_remained() {

		return data_remained;
	}


	public double getNb_packets() {
		return nb_packets;
	}






}
