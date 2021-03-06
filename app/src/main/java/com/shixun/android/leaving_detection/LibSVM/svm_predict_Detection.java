package com.shixun.android.leaving_detection.LibSVM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_print_interface;

public class svm_predict_Detection {
	private static svm_print_interface svm_print_null = new svm_print_interface() {
		public void print(String s) {}
	};

	private static svm_print_interface svm_print_stdout = new svm_print_interface() {
		public void print(String s) {
			System.out.print(s);
		}
	};

	private static svm_print_interface svm_print_string = svm_print_stdout;

	static void info(String s) {
		svm_print_string.print(s);
	}

	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private static double[] predict(String input, svm_model model, int predict_probability) throws IOException {
		int correct = 0;
		int t_p = 0, f_p = 0, f_n = 0;
		int total = 0;
		double error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] result = new double[2];

		int svm_type=svm.svm_get_svm_type(model);
		int nr_class=svm.svm_get_nr_class(model);
		double[] prob_estimates=null;

		if(predict_probability == 1) {
			if(svm_type == svm_parameter.EPSILON_SVR ||
			   svm_type == svm_parameter.NU_SVR) {
				svm_predict_Detection.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
			}
			else {
				int[] labels=new int[nr_class];
				svm.svm_get_labels(model,labels);
				prob_estimates = new double[nr_class];
				//output.writeBytes("labels");
				//for(int j=0;j<nr_class;j++)
				//	output.writeBytes(" "+labels[j]);
				//output.writeBytes("\n");
			}
		}

			//String line = input.readLine();
			String line = input;
			//if(line == null) break;

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			double v;
			if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC)) {
				v = svm.svm_predict_probability(model,x,prob_estimates);
				//output.writeBytes(v+" ");
				//for(int j=0;j<nr_class;j++)
				//	output.writeBytes(prob_estimates[j]+" ");
//				//output.writeBytes("\n");
//				if (prob_estimates[1] >=0.95) {
//					v = 1.0;
//				} else {
//					v = 0.0;
//				}
			}
			else {
				v = svm.svm_predict(model,x);
				//output.writeBytes(v+"\n");
			}

			if(v == target) {
				++correct;
			}
			if(v == 1 && v != target)
				++f_p;
			if(v == 1 && v == target)
				++t_p;
			if(v != 1 && v != target)
				++f_n;
			error += (v-target)*(v-target);
			sumv += v;
			sumy += target;
			sumvv += v*v;
			sumyy += target*target;
			sumvy += v*target;
			++total;

			result[0] = v;
			if(v == 100.0) {
				List<Double> list = new ArrayList<Double>();
				for (int i = 0; i < prob_estimates.length; i++) {
					list.add(prob_estimates[i]);
				}
				result[1] = Collections.max(list);
			} else {
				result[1] = 0;
			}

		if(svm_type == svm_parameter.EPSILON_SVR ||
		   svm_type == svm_parameter.NU_SVR) {
			svm_predict_Detection.info("Mean squared error = "+error/total+" (regression)\n");
			svm_predict_Detection.info("Squared correlation coefficient = "+
				 ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
				 ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
				 " (regression)\n");
		}
		else {
			svm_predict_Detection.info("Accuracy = " + (double) correct / total * 100 +
					"% (" + correct + "/" + total + ") (classification)\n");
			svm_predict_Detection.info("Precision = "+(double) t_p /(t_p + f_p) * 100 + "%\n");
			svm_predict_Detection.info("Recall = "+(double) t_p / (t_p + f_n) * 100 + "%\n");
			svm_predict_Detection.info("Possiblity = "+prob_estimates[1]+"%\n");
		}

		return result;
	}

	private static void exit_with_help() {
		System.err.print("usage: svm_predict_Detection [options] test_file model_file output_file\n"
		+"options:\n"
		+"-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
		+"-q : quiet mode (no outputs)\n");
		System.exit(1);
	}

	public static double[] main(String argv[], String input_self, BufferedReader model_self) throws IOException {
		int i, predict_probability=0;
		double[] result = null;
        	svm_print_string = svm_print_stdout;

		// parse options
		for(i=0;i<argv.length;i++) {
			if(argv[i].charAt(0) != '-') break;
			++i;
			switch(argv[i-1].charAt(1)) {
				case 'b':
					predict_probability = atoi(argv[i]);
					break;
				case 'q':
					svm_print_string = svm_print_null;
					i--;
					break;
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}
		if(i>=argv.length-2)
			exit_with_help();
		try {
			//BufferedReader input = new BufferedReader(new FileReader(argv[i]));
			String input = input_self;
			//DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(argv[i+2])));
			svm_model model = svm.svm_load_model(model_self);
			if (model == null) {
				System.err.print("can't open model file "+argv[i+1]+"\n");
				System.exit(1);
			}
			if(predict_probability == 1) {
				if(svm.svm_check_probability_model(model)==0) {
					System.err.print("Model does not support probabiliy estimates\n");
					System.exit(1);
				}
			}
			else {
				if(svm.svm_check_probability_model(model)!=0) {
					svm_predict_Detection.info("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
			result = predict(input,model,predict_probability);
			//input.close();
			//output.close();

		}
		catch(FileNotFoundException e) {
			exit_with_help();
		}
		catch(ArrayIndexOutOfBoundsException e) {
			exit_with_help();
		}

		return result;
	}
}
