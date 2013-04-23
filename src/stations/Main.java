package stations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.String;


import excel.Gestion_excel;


public class Main {

	/**
	 * @param args
	 **/



	private static   int nb_stations;
	private static   int nb_stations_54;
	private static   int nb_stations_36;
	private static   int nb_stations_11;
	private static   int nb_stations_1;
	private static int nb_fragment=10;
	static double time_max_data = Calcul.calculer_transmission(1500*8.0/nb_fragment,1.0,false,false)[0];
	private static final int Number_iterations=3;
	private static  boolean time_limited=true;
	private static  int duree_limite=1000;

	private static boolean full_optimized=false;
	public static int colum=3;

	public static void main(String[] args) throws IOException {




		/*-----------------------Obtention des paramètres necessaire auprès de l'utilisateur---------------------------*/

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 		
		System.out.print("nb stations 54 Mbps? ");
		String text = in.readLine();
		try{
			nb_stations_54=Integer.parseInt(text);
			if (nb_stations_54<0)
				nb_stations_54=0;
		}
		catch(NumberFormatException e){
			nb_stations_54=9;
		}
		System.out.print("nb stations 36 Mbps? ");
		text = in.readLine();
		try{
			nb_stations_36= Integer.parseInt(text);
			if (nb_stations_36<0)
				nb_stations_36=0;

		}
		catch(NumberFormatException e){

			nb_stations_36=0;
		}
		System.out.print("nb stations 11 Mbps? ");
		text = in.readLine();
		try{
			nb_stations_11= Integer.parseInt(text);
			if (nb_stations_11<0)
				nb_stations_11=0;

		}
		catch(NumberFormatException e){

			nb_stations_11=0;
		}
		System.out.print("nb stations 1 Mbps? ");
		text = in.readLine();
		try{
			nb_stations_1= Integer.parseInt(text);
			if (nb_stations_1<0)
				nb_stations_1=0;

		}
		catch(NumberFormatException e){

			nb_stations_1=1;
		}

		nb_stations = nb_stations_54+nb_stations_36 +nb_stations_11+nb_stations_1;
		if(nb_stations<1)
			return;
		
		System.out.print("\tduree de simulation (secondes)? ");
		text = in.readLine();
		try{
			duree_limite=Integer.parseInt(text);
			if (duree_limite<0)
				duree_limite=0;
		}
		catch(NumberFormatException e){
			duree_limite=1000;
		}


		System.out.print("Temps de transmission (�s) ? ");
		text = in.readLine();
		try{
			time_max_data=Double.parseDouble(text);
			if (time_max_data<1)
				time_max_data=Calcul.calculer_transmission(1500*8.0/nb_fragment,1.0,false,false)[0];
		}
		catch(NumberFormatException e){
			time_max_data=2044;
		}
		in.close();





		/*---------------------------------------------------------------------------------------------------------------------*/






		/*---------------------------------------------------------------------------------------------------------------------*/


		/*---------------------------------affichage des paramètres------------------------------------------------------------*/
		System.out.println("\nParametres:");
		System.out.println();
		System.out.println("\t"+nb_stations +" stations");
		System.out.println("\t"+(Math.round(((double)nb_stations_54/(double)nb_stations) * Math.pow(10,4)) / Math.pow(10,2)) +" % des stations en mode 54 Mb/s");
		System.out.println("\t"+ (Math.round(((double)nb_stations_36/(double)nb_stations) * Math.pow(10,4)) / Math.pow(10,2)) +" % des stations en mode 36 Mb/s");
		System.out.println("\t"+ (Math.round(((double)nb_stations_11/(double)nb_stations) * Math.pow(10,4)) / Math.pow(10,2)) +" % des stations en mode 11 Mb/s");
		System.out.println("\t"+ (Math.round((1-(((double)nb_stations_54+(double)nb_stations_36+(double)nb_stations_11)/(double)nb_stations)) * Math.pow(10,4)) / Math.pow(10,2)) +" % des stations en mode 1 Mb/s");
		System.out.println();
		System.out.println("\tTemps limite: "+ (time_limited?"oui":"non"));
		if(time_limited)
			System.out.println("\t\tDuree limite: "+ duree_limite +" s");

		System.out.println();
		System.out.println("Temps de transmission (�s): "+time_max_data);
		System.out.println();


		/*---------------------------------------------------------------------------------------------------------------------*/


		/*---------------------------------Création des stations---------------------------------------------------------------*/

		ArrayList<Station> stations = new ArrayList<Station>();

		for(int i=0;i<nb_stations_54;i++){

			stations.add(new Station(54.0));

		}
		for(int i=0;i<nb_stations_36;i++){

			stations.add(new Station(36.0));

		}

		for(int i=0;i<nb_stations_11;i++){

			stations.add(new Station(11.0));

		}
		for(int i=0;i<nb_stations_1;i++){

			stations.add(new Station(1.0));

		}

		/*---------------------------------------------------------------------------------------------------------------------*/


		/*---------------------------------Simulation--------------------------------------------------------------------------*/
		/*	stations.add(new Station(1.0));
		Main.nb_stations=1;
		Main.nb_stations_54=0;
		for (int i=2;i<21;i++){

			//stations.add(new Station(1.0,ratio_data_total));


			if(i%4==2){
				stations.add(new Station(1.0));
				Main.nb_stations+=1;}
			else if(i%4==0){
				stations.remove(i-3);
				Main.nb_stations--;
				for(int y=0;y<2;y++){
					stations.add(new Station(54.0));
					Main.nb_stations++;
					Main.nb_stations_54++;
				}
			}
			else{
				stations.add(new Station(54.0));
				Main.nb_stations+=1;
				Main.nb_stations_54+=1;
			}



		 */
		simulation(stations,true); // simulation mode optimisé
		simulation(stations,false); // simulation mode normal
		full_optimized=true;
		simulation(stations,true); // simulation mode optimisé
		full_optimized=false;
		colum++;




	}

	private static void simulation(ArrayList<Station> stations,boolean optimized){



		double time;  //temps ecoulé pendant une simulation
		double time_54; //temps écoulé par les station 54 Mb/s lors d'acces exclusif au canal
		double data_54; //data transmise par les station 54 Mb/s lors d'acces exclusif au canal

		double time_1; //temps écoulé par les station 1 Mb/s lors d'acces exclusif au canal
		double data_1;//data transmise par les station 1 Mb/s lors d'acces exclusif au canal

		double time_11; //temps écoulé par les station 1 Mb/s lors d'acces exclusif au canal
		double data_11;//data transmise par les station 1 Mb/s lors d'acces exclusif au canal

		double time_36; //temps écoulé par les station 1 Mb/s lors d'acces exclusif au canal
		double data_36;//data transmise par les station 1 Mb/s lors d'acces exclusif au canal
		
		int access_canal_54=0; //nb d'acces exclusif canal (stations 54)
		int access_canal_36=0; //nb d'acces exclusif canal (stations 36)
		int access_canal_11=0; //nb d'acces exclusif canal (stations 11)
		int access_canal_1=0; //nb d'acces exclusif canal (stations 1)

		int data_remained;
		int time_slot=20;
		double debit=0;



		double transmission_time=0;

		double moyenne_ratio_data_54 = 0;
		double moyenne_Throughputwooh_54 = 0;
		double moyenne_Throughputwoh_54 = 0;
		double moyenne_ratio_data_36 = 0;
		double moyenne_Throughputwooh_36 = 0;
		double moyenne_Throughputwoh_36 = 0;
		double moyenne_ratio_data_11 = 0;
		double moyenne_Throughputwooh_11 = 0;
		double moyenne_Throughputwoh_11 = 0;
		double moyenne_ratio_data_1 = 0;
		double moyenne_Throughputwooh_1 = 0;
		double moyenne_Throughputwoh_1 = 0;


		double moy_data_54=0;
		double moy_data_1=0;
		double moy_data_11=0;
		double moy_data_36=0;
		
		double moy_access_canal_54=0;
		double moy_access_canal_11=0;
		double moy_access_canal_1=0;
		double moy_access_canal_36=0;
		
		double moy_time=0;
		double moy_time_54=0;
		double moy_time_11=0;
		double moy_time_1=0;
		double moy_time_36=0;

		
		double time_collision=0;
		double proba_colisions=0;
		double proba_send_packets=0;
		double cwmoy_toprint=0;


		int min_backoff;
		Station station_emettrice;

		ArrayList<Integer> stations_emettrices=new ArrayList<Integer>();


		for (int m=0;m<Number_iterations;m++){     // on simule plusieurs fois.


			for(int i=0;i<nb_stations;i++){  // on reinitialise les stations avant le début de chaque simulation
				stations.get(i).reinitialiser();

			}

			time=0;
			time_54=0;
			data_54=0;
			time_36=0;
			data_36=0;
			time_1=0;
			data_1=0;
			time_11=0;
			data_11=0;
			access_canal_54=0;
			access_canal_36=0;
			access_canal_1=0;
			access_canal_11=0;

			for (int y=0;y<Integer.MAX_VALUE;y++){  


				/*----------------------Decouverte du nombre de stations encore en "jeu" et combien d'entre elle veulent emettre un paquet-----------------------------*/



				min_backoff=Integer.MAX_VALUE;
				stations_emettrices.clear();

				for (int i=0;i<nb_stations;i++){




					min_backoff=Math.min(stations.get(i).getBackoff(), min_backoff);

					if(stations.get(i).getBackoff()==0){

						stations_emettrices.add(i); 
					}



				}




				/*----------------------Detection fin de simulation------------------------------------*/

				if (time_limited){
					if (time>=duree_limite){
						break;
					}
				}


				/*----------------------Detection d'un "tour"------------------------------------*/

				if (stations_emettrices.size()==0){ //aucune station veulent emettre

					time+=(min_backoff * time_slot)/(1000000.0);


					for (int i=0;i<nb_stations;i++){

						stations.get(i).decompter_backoff(min_backoff);


					}


				}

				else if(stations_emettrices.size()==1){ //une seule station veut emettre => elle emet



					station_emettrice=stations.get(stations_emettrices.get(0));
					station_emettrice.new_backoff();



					data_remained= station_emettrice.getData_remained();  // on regarde la quantité de donnees du paquezt restante à envoyer
					debit=station_emettrice.getDebit(); //on recupere son debit

					/*----------------Calcul du temps pris par la station----------------------*/
					int data_transmitted=0;

					double[] temp=null;

					double nb_packets=0;
					nb_packets = ((Calcul.retrouver_data(Main.time_max_data, debit)[1]/8.0)/1500);
					if(!full_optimized){
						if(!optimized)
							nb_packets=1;
						else
							nb_packets=Math.min(nb_packets, 1.0);
					}
					else{
						if(nb_packets>1.0){
							nb_packets = Math.floor(nb_packets);
						}
					}
					data_transmitted=(int)(nb_packets*8.0*1500);
					temp=Calcul.calculer_transmission(data_transmitted, debit, optimized,false);
					transmission_time=temp[0];



					/*-------------------------------------------------------------------------*/

					time+=transmission_time/(1000000.0);  //on ajoute ce temps

					station_emettrice.data_remained(data_remained-data_transmitted) ;	//on retire la quantité de donnees transmise.



					/*-----------------------actualisation des compteurs pour les statistiques --------------------------*/
					if(debit==54){
						access_canal_54++;
						data_54+=data_transmitted/8.0;
						time_54+=temp[2]/(1000000.0);
					}
					else if(debit==36){
						access_canal_36++;
						data_36+=data_transmitted/8.0;
						time_36+=temp[2]/(1000000.0);
					}
					else if(debit==11){
						access_canal_11++;
						data_11+=data_transmitted/8.0;
						time_11+=temp[2]/(1000000.0);
					}
					else{
						access_canal_1++;
						data_1+=data_transmitted/8.0;
						time_1+=temp[2]/(1000000.0);
					}


					/*---------------------------------------------------------------------------------------------------*/


				}


				else if(stations_emettrices.size()>1){   // + d'une station veulent emettre => collision

					time_collision=0;
					for (int i=0;i<stations_emettrices.size();i++){  //parmies celles en jeu et qui veulent emetttre


						station_emettrice=stations.get(stations_emettrices.get(i));


						/*----------------- on calcul le temps maximal qui aurait été pris lors de cette collision-------------*/

						double time_collision_temp;
						debit = station_emettrice.getDebit();

						int data_transmitted=0;
						double nb_packets=0;
						nb_packets = ((Calcul.retrouver_data(Main.time_max_data, debit)[1]/8.0)/1500);
						if(!full_optimized){
							if(!optimized)
								nb_packets=1;
							else
								nb_packets=Math.min(nb_packets, 1.0);
						}
						else{
							if(nb_packets>1.0){
								nb_packets = Math.floor(nb_packets);
							}
						}
						data_transmitted=(int)(nb_packets*8.0*1500);
						time_collision_temp=Calcul.calculer_transmission(data_transmitted,debit, optimized,false)[0];

						/*if (full_optimized){
							if(debit==54){
								int data_transmitted=(int)((Calcul.retrouver_data(Main.time_max_data, debit)[1]/8.0)/1500);
								data_transmitted*=1500*8;
								time_collision_temp=Calcul.calculer_transmission(data_transmitted,debit, optimized,false)[0];

							}
							else{
								time_collision_temp=Calcul.calculer_transmission(station_emettrice.getData_remained(),debit, optimized,false)[0];

							}
						}
						else{
							time_collision_temp=Calcul.calculer_transmission(station_emettrice.getData_remained(), debit, optimized,false)[0];
						}*/

						//time_ack= Main.calculer_transmission(0, station_emettrice.getDebit(), optimized,true)[0];

						time_collision=Math.max(time_collision, time_collision_temp);

						/*----------------- -----------------------------------------------------------------------------------*/
						station_emettrice.collision(); // CW*=2 + nouveau backoff


					}
					/*----------------- on calcul le temps suivant le mode qui aurait été pris lors de cette collision-------------*/


					time+= (time_collision)/(1000000.0);

					/*----------------- -----------------------------------------------------------------------------------*/





				}
			}





			/*--------------------------------------Calcul pour les statistiques---------------------------------------*/
			moyenne_Throughputwooh_54+=((data_54*8.0)/time)/(1000000.0); //time_to_send_frames_54
			moyenne_Throughputwoh_54+=(time_54/time)*54.0;//time_to_send_frames_54
			moyenne_ratio_data_54+=(data_54/(data_54+data_36+data_11+data_1));

			moyenne_Throughputwooh_1+=((data_1*8.0)/time)/(1000000.0);//time_to_send_frames_1
			moyenne_Throughputwoh_1+=(time_1/time);//time_to_send_frames_1
			moyenne_ratio_data_1+=(data_1/(data_54+data_36+data_11+data_1));

			moyenne_Throughputwooh_11+=((data_11*8.0)/time)/(1000000.0);//time_to_send_frames_1
			moyenne_Throughputwoh_11+=(1-0.1666)*(time_11/time)*11 + 0.1666*(time_11/time);//time_to_send_frames_1
			moyenne_ratio_data_11+=(data_11/(data_54+data_36+data_11+data_1));
			
			moyenne_Throughputwooh_36+=((data_36*8.0)/time)/(1000000.0);//time_to_send_frames_1
			moyenne_Throughputwoh_36+=(time_36/time)*36;//time_to_send_frames_1
			moyenne_ratio_data_36+=(data_11/(data_54+data_36+data_11+data_1));


			moy_access_canal_54+=(double)access_canal_54/(double)(access_canal_54+access_canal_36+access_canal_11+access_canal_1);
			moy_access_canal_11+=(double)access_canal_11/(double)(access_canal_54+access_canal_36+access_canal_11+access_canal_1);
			moy_access_canal_1+=(double)access_canal_1/(double)(access_canal_54+access_canal_36+access_canal_11+access_canal_1);
			moy_access_canal_36+=(double)access_canal_36/(double)(access_canal_54+access_canal_36+access_canal_11+access_canal_1);

			moy_data_54+=data_54/(1000000.0);
			moy_data_1+=data_1/(1000000.0);
			moy_data_36+=data_36/(1000000.0);
			moy_data_11+=data_11/(1000000.0);

			moy_time+=time;
			moy_time_54+=time_54;
			moy_time_1+=time_1;
			moy_time_36+=time_36;
			moy_time_11+=time_11;

			double pr_s = 0;
			double pr_c=0;
			double cwmoy=0;
			for(int h=0; h<Main.nb_stations;h++){
				pr_s+=(stations.get(h).nb_slots_sending/stations.get(h).nb_slots);
				pr_c+=(stations.get(h).nb_slots_collisions/stations.get(h).nb_slots_sending);
				cwmoy+=(stations.get(h).cwmean/stations.get(h).nb_slots_sending);
			}
			cwmoy/=Main.nb_stations;
			pr_s/=Main.nb_stations;
			pr_c/=Main.nb_stations;
			proba_send_packets+=pr_s;
			proba_colisions+=pr_c;
			cwmoy_toprint+=cwmoy;
			/*---------------------------------------------------------------------------------------------------------*/


		}	

		/*--------------------------------------Calcul pour les statistiques---------------------------------------*/


		moy_data_54=(moy_data_54/Number_iterations);
		moy_data_54 = Math.round(moy_data_54 * Math.pow(10,3)) / Math.pow(10,3);
		moy_data_11=(moy_data_11/Number_iterations);
		moy_data_11 = Math.round(moy_data_11 * Math.pow(10,3)) / Math.pow(10,3);
		moy_data_36=(moy_data_36/Number_iterations);
		moy_data_36 = Math.round(moy_data_36 * Math.pow(10,3)) / Math.pow(10,3);
		moy_data_1=(moy_data_1/Number_iterations);
		moy_data_1 = Math.round(moy_data_1 * Math.pow(10,3)) / Math.pow(10,3);

		moy_time=(moy_time/Number_iterations);
		moy_time = Math.round(moy_time * Math.pow(10,3)) / Math.pow(10,3);
		moy_time_54=(moy_time_54/Number_iterations);
		moy_time_54 = Math.round(moy_time_54 * Math.pow(10,3)) / Math.pow(10,3);
		moy_time_1=(moy_time_1/Number_iterations);
		moy_time_1 = Math.round(moy_time_1 * Math.pow(10,3)) / Math.pow(10,3);
		moy_time_11=(moy_time_11/Number_iterations);
		moy_time_11 = Math.round(moy_time_11 * Math.pow(10,3)) / Math.pow(10,3);
		moy_time_36=(moy_time_36/Number_iterations);
		moy_time_36 = Math.round(moy_time_36 * Math.pow(10,3)) / Math.pow(10,3);

		moy_access_canal_54=(moy_access_canal_54/Number_iterations)*100;
		moy_access_canal_54 = Math.round(moy_access_canal_54 * Math.pow(10,3)) / Math.pow(10,3);
		moy_access_canal_11=(moy_access_canal_11/Number_iterations)*100;
		moy_access_canal_11= Math.round(moy_access_canal_11 * Math.pow(10,3)) / Math.pow(10,3);
		moy_access_canal_36=(moy_access_canal_36/Number_iterations)*100;
		moy_access_canal_36= Math.round(moy_access_canal_36 * Math.pow(10,3)) / Math.pow(10,3);
		moy_access_canal_1=(moy_access_canal_1/Number_iterations)*100;
		moy_access_canal_1 = Math.round(moy_access_canal_1 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwoh_54=(moyenne_Throughputwoh_54/(Number_iterations*nb_stations_54));
		moyenne_Throughputwoh_54 = Math.round(moyenne_Throughputwoh_54 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwooh_54=moyenne_Throughputwooh_54/(Number_iterations*nb_stations_54);
		moyenne_Throughputwooh_54 = Math.round(moyenne_Throughputwooh_54 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_ratio_data_54=(moyenne_ratio_data_54/Number_iterations)*100;
		moyenne_ratio_data_54 = Math.round(moyenne_ratio_data_54 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwoh_11=(moyenne_Throughputwoh_11/(Number_iterations*nb_stations_11));
		moyenne_Throughputwoh_11 = Math.round(moyenne_Throughputwoh_11 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwooh_11=moyenne_Throughputwooh_11/(Number_iterations*nb_stations_11);
		moyenne_Throughputwooh_11 = Math.round(moyenne_Throughputwooh_11 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_ratio_data_11=(moyenne_ratio_data_11/Number_iterations)*100;
		moyenne_ratio_data_11 = Math.round(moyenne_ratio_data_11 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwoh_36=(moyenne_Throughputwoh_36/(Number_iterations*nb_stations_36));
		moyenne_Throughputwoh_36 = Math.round(moyenne_Throughputwoh_36 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwooh_36=moyenne_Throughputwooh_36/(Number_iterations*nb_stations_36);
		moyenne_Throughputwooh_36 = Math.round(moyenne_Throughputwooh_36 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_ratio_data_36=(moyenne_ratio_data_36/Number_iterations)*100;
		moyenne_ratio_data_36 = Math.round(moyenne_ratio_data_36 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwoh_1=(moyenne_Throughputwoh_1/(Number_iterations*nb_stations_1));
		moyenne_Throughputwoh_1 = Math.round(moyenne_Throughputwoh_1 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_Throughputwooh_1=moyenne_Throughputwooh_1/(Number_iterations*nb_stations_1);
		moyenne_Throughputwooh_1 = Math.round(moyenne_Throughputwooh_1 * Math.pow(10,3)) / Math.pow(10,3);

		moyenne_ratio_data_1=(moyenne_ratio_data_1/Number_iterations)*100;
		moyenne_ratio_data_1 = Math.round(moyenne_ratio_data_1 * Math.pow(10,3)) / Math.pow(10,3);


		proba_colisions=(proba_colisions/Number_iterations);
		proba_colisions = Math.round(proba_colisions * Math.pow(10,3)) / Math.pow(10,3);
		proba_send_packets=(proba_send_packets/Number_iterations);
		proba_send_packets = Math.round(proba_send_packets * Math.pow(10,3)) / Math.pow(10,3);
		cwmoy_toprint=(cwmoy_toprint/Number_iterations);
		cwmoy_toprint = Math.round(cwmoy_toprint * Math.pow(10,3)) / Math.pow(10,3);
		/*---------------------------------------------------------------------------------------------------------*/



		/*--------------------------------------Affichage résultat---------------------------------------*/
		if (optimized){

			if(full_optimized)
				System.out.println("Mode Optimise avec agr�gation\n");
			else
				System.out.println("Mode Optimise\n");
		}
		else
			System.out.println("Mode normal\n");


		if(Main.nb_stations_54>0){
			System.out.println("\t les stations 54 Mbps:");
			//System.out.println("\t\tThroughput with overhead : " + moyenne_Throughputwoh_54 +" Mbps");
			System.out.println("\t\tThroughput without overhead : " + moyenne_Throughputwooh_54 +" Mbps");
			System.out.println("\t\tData sent: " + moy_data_54 +" MB");
			System.out.println("\t\tRatio data: " + moyenne_ratio_data_54 +" %");
			System.out.println("\t\tAcces au canal: "+ moy_access_canal_54 +" %");
			System.out.println("\t\tTemps ecoule sur le canal a envoyer des donnees (entete comprises, collisions non comprises): "+ moy_time_54 +" s");
			System.out.println("\t\tTemps total (entete comprises, collisions comprises): "+ moy_time + " s");
			System.out.println();
		}
		if(Main.nb_stations_36>0){
			System.out.println("\t les stations 36 Mbps:");
			//System.out.println("\t\tThroughput with overhead  : " + moyenne_Throughputwoh_36 +" Mbps");
			System.out.println("\t\tThroughput without overhead : " + moyenne_Throughputwooh_36 +" Mbps");
			System.out.println("\t\tData sent: " + moy_data_36 +" MB");
			System.out.println("\t\tRatio data: " + moyenne_ratio_data_36 +" %");
			System.out.println("\t\tAcces au canal: "+ moy_access_canal_36 +" %");
			System.out.println("\t\tTemps ecoule sur le canal a envoyer des donnees (entete comprises, collisions non comprises): "+ moy_time_36 +" s");
			System.out.println("\t\tTemps total (entete comprises, collisions comprises): "+ moy_time + " s");
			System.out.println();
		}
		if(Main.nb_stations_11>0){
			System.out.println("\t les stations 11 Mbps:");
			//System.out.println("\t\tThroughput with overhead  : " + moyenne_Throughputwoh_11 +" Mbps");
			System.out.println("\t\tThroughput without overhead : " + moyenne_Throughputwooh_11 +" Mbps");
			System.out.println("\t\tData sent: " + moy_data_11 +" MB");
			System.out.println("\t\tRatio data: " + moyenne_ratio_data_11 +" %");
			System.out.println("\t\tAcces au canal: "+ moy_access_canal_11 +" %");
			System.out.println("\t\tTemps ecoule sur le canal a envoyer des donnees (entete comprises, collisions non comprises): "+ moy_time_11 +" s");
			System.out.println("\t\tTemps total (entete comprises, collisions comprises): "+ moy_time + " s");
			System.out.println();
		}
		if(Main.nb_stations_1>0){
			System.out.println("\t les stations 1 Mbps:");
			//System.out.println("\t\tThroughput with overhead  : " + moyenne_Throughputwoh_1 +" Mbps");
			System.out.println("\t\tThroughput without overhead : " + moyenne_Throughputwooh_1 +" Mbps");
			System.out.println("\t\tData sent: " + moy_data_1 +" MB");
			System.out.println("\t\tRatio data: " + moyenne_ratio_data_1 +" %");
			System.out.println("\t\tAcces au canal: "+ moy_access_canal_1 +" %");
			System.out.println("\t\tTemps ecoule sur le canal a envoyer des donnees (entete comprises, collisions non comprises): "+ moy_time_1 +" s");
			System.out.println("\t\tTemps total (entete comprises, collisions comprises): "+ moy_time + " s");
			System.out.println();
		}
		System.out.println("Probabilit� d'�mission: " + proba_send_packets);
		System.out.println("Probabilit� de colisions: " + proba_colisions);
		System.out.println(cwmoy_toprint);
		System.out.println("\n");


		/*---------------------------------------------------------------------------------------------------------*/

		/*	Calcul.initialized_facTab(Main.nb_stations);
		double ratio_sta_54 = (double)Main.nb_stations_54/(double)Main.nb_stations;

		Gestion_excel.create("C:\\Users\\Toni\\Dropbox\\R�seau\\projet libre\\copie.xlsx");
		if(!optimized && !full_optimized){
			Gestion_excel.ecrire_cellule(0,8,3,moyenne_Throughputwooh_54);

			Gestion_excel.ecrire_cellule(0,8,4,moyenne_Throughputwooh_1);

			Gestion_excel.ecrire_cellule(4,13,colum,moyenne_Throughputwooh_54);
			Gestion_excel.ecrire_cellule(4,7,colum,moyenne_Throughputwooh_1);

			Gestion_excel.ecrire_cellule(4,21,colum,(moy_time_54+moy_time_1)/moy_time);
			Gestion_excel.ecrire_cellule(4,22,colum,((1-ratio_sta_54)*(Calcul.calculer_transmission(1500*8.0,1.0,optimized,false)[0]) + ratio_sta_54*(Calcul.calculer_transmission(1500*8.0,54.0,optimized,false)[0]))/20.0);

		}
		if(optimized && full_optimized){
			Gestion_excel.ecrire_cellule(0,6,3,moyenne_Throughputwooh_54);
			Gestion_excel.ecrire_cellule(0,6,4,moyenne_Throughputwooh_1);

			Gestion_excel.ecrire_cellule(4,5,colum,moyenne_Throughputwooh_1);
			Gestion_excel.ecrire_cellule(4,11,colum,moyenne_Throughputwooh_54);
			Gestion_excel.ecrire_cellule(4,18,colum,(moy_time_54+moy_time_1)/moy_time);

			Gestion_excel.ecrire_cellule(4,19,colum,((1-ratio_sta_54)*(Calcul.calculer_transmission(1500*8.0,1.0,optimized,false)[0]) + ratio_sta_54*(Calcul.calculer_transmission(1500*8.0*8.0,54.0,optimized,false)[0]))/20.0);

		}
		if(optimized && !full_optimized){

			Gestion_excel.ecrire_cellule(4,15,colum,(moy_time_54+moy_time_1)/moy_time);

			Gestion_excel.ecrire_cellule(4,16,colum,((1-ratio_sta_54)*(Calcul.calculer_transmission(1500*8.0,1.0,optimized,false)[0]) + ratio_sta_54*(Calcul.calculer_transmission(1500*8.0,54.0,optimized,false)[0]))/20.0);



			//Update the value of cell


			double p=Calcul.real_cons(Main.nb_stations)[0];
			if(p==-1)
				p=proba_send_packets;
			Gestion_excel.ecrire_cellule(1,2,1,p);
			//Gestion_excel.ecrire_cellule(1,2,7,(1/temp[0])-1);


			Gestion_excel.ecrire_cellule(1,2,3,Main.nb_stations_54);
			Gestion_excel.ecrire_cellule(1,2,4,Main.nb_stations-Main.nb_stations_54);



			Gestion_excel.ecrire_cellule(1,10,1,Calcul.p_collision_54(p, Main.nb_stations-Main.nb_stations_54-1, Main.nb_stations_54,2)[1]);

			Gestion_excel.ecrire_cellule(1,21,1,Calcul.p_collision_54(p, Main.nb_stations-Main.nb_stations_54, Main.nb_stations_54-1,2)[1]);
			Gestion_excel.ecrire_cellule(1,24,1,Calcul.p_collision_54(p, Main.nb_stations-Main.nb_stations_54, Main.nb_stations_54-1,1)[1]);



			//Gestion_excel.ecrire_cellule(1,2,7,cwmoy_toprint);



			Gestion_excel.ecrire_cellule(0,4,3,moyenne_Throughputwooh_54);

			Gestion_excel.ecrire_cellule(0,4,4,moyenne_Throughputwooh_1);

			Gestion_excel.ecrire_cellule(4,3,colum,moyenne_Throughputwooh_1);
			Gestion_excel.ecrire_cellule(4,9,colum,moyenne_Throughputwooh_54);

			Gestion_excel.ecrire_cellule(4,1,colum,Main.nb_stations);
			Gestion_excel.ecrire_cellule(4,14,colum,Main.nb_stations);

			Gestion_excel.evaluate();

			Gestion_excel.ecrire_cellule(4,2,colum,Gestion_excel.getvalue(0, 3, 4));
			Gestion_excel.ecrire_cellule(4,8,colum,Gestion_excel.getvalue(0, 3, 3));

			Gestion_excel.ecrire_cellule(4,4,colum,Gestion_excel.getvalue(0, 5, 4));
			Gestion_excel.ecrire_cellule(4,10,colum,Gestion_excel.getvalue(0, 5, 3));

			Gestion_excel.ecrire_cellule(4,6,colum,Gestion_excel.getvalue(0, 7, 4));
			Gestion_excel.ecrire_cellule(4,12,colum,Gestion_excel.getvalue(0, 7, 3));


		}

		Gestion_excel.close();



		 */

	}
}

