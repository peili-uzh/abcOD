package org.music.abcod;

import org.music.data.Pipe;

import java.util.*;

public class Piece extends Block {

	public Piece(LinkedHashMap<Integer, Integer> timelist) throws Exception {
		super(timelist);
		// TODO Auto-generated constructor stub
	}

	public Piece(int start, int end) {
	}

	public Piece() {
	}

	;

	public int start;

	public int end;

	public double gain;

	public ArrayList<Integer> lmb;

	public ArrayList<Integer> errors;

	public int[][] set_lib;
	public int[][] set_errors;

	public int maxErrors;

	public int size; // # of records in the piece

	public boolean increase;

	public boolean change = true;


	public boolean isChange() {
		return change;
	}

	public void setChange(boolean change) {
		this.change = change;
	}

	public boolean isIncrease() {
		return increase;
	}

	public void setIncrease(boolean increase) {
		this.increase = increase;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}


	public ArrayList<Integer> getLMB() {
		return lmb;
	}

	public void setLMB(ArrayList<Integer> lmb) {
		this.lmb = lmb;
	}

	public ArrayList<Integer> getErrors() {
		return errors;
	}

	public void setErrors(ArrayList<Integer> errors) {
		this.errors = errors;
	}


	public int[][] getSet_lib() {
		return set_lib;
	}

	public void setSet_lib(int[][] set_lib) {
		this.set_lib = set_lib;
	}

	public int[][] getSet_errors() {
		return set_errors;
	}

	public void setSet_errors(int[][] set_errors) {
		this.set_errors = set_errors;
	}

	public int getSize(ArrayList<HashSet> set_sequence) {
		//System.out.println("check for piece size");
		for (int i = start; i <= end; i++) {
			HashSet years = set_sequence.get(i);

			int max_year = getMaxYear(years);
			if (max_year > 0)
				size++;
			//System.out.println("\t"+i+"\t"+years+"\t"+size);
		}
		return size;
	}


	public int findMax_set_errors(int[][] lib_errors) {

		int max = 1;
		int count = 1;

		for (int j = 0; j < lib_errors.length - 1; j++) {
			int index = lib_errors[j][0];
			int next_index = lib_errors[j + 1][0];
			if ((index + 1) == next_index)
				count++;
			else count = 1;
			max = Math.max(max, count);
		}

		if (lib_errors.length == 0)
			max = 0;


		return max;
	}

	public void setMaxErrors(int maxErrors) {
		this.maxErrors = maxErrors;
	}


	public int getMaxErrors() {
		return maxErrors;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<Integer> findLIS(ArrayList<Integer> sequence, int deltat) {

		ArrayList<Integer> lis = new ArrayList<Integer>();
		int n = sequence.size();


		/*
		 * 1. a size array to keep track of the longest LIS ending with current position
		 * 2. an accordingly string array to keep track of the path for printing out
		 * 3. index array list to keep the entry index of each path
		 */
		String[] paths = new String[n];
		int[] sizes = new int[n];
		ArrayList<ArrayList> indexs = new ArrayList<ArrayList>();

		/*
		 * 1. assign the initial values to each path/size, by setting size to 1 and path equal to the value, i.e., initially each path starting/ending with its current position
		 *    exclude missing year (year = 0)
		 */
		for (int i = start; i <= end; i++) {
			if (sequence.get(i) == 0) { // if time = 0, size = 0
				sizes[i - start] = 0;
				paths[i - start] = "";
				ArrayList<Integer> list = new ArrayList<Integer>();
				indexs.add(i - start, list);
			} else {
				sizes[i - start] = 1;
				paths[i - start] = sequence.get(i - start) + " ";
				ArrayList<Integer> list = new ArrayList<Integer>();
				list.add(list.size(), i);
				indexs.add(i - start, list);
			}


		}
		//System.out.println("indexs \t"+indexs);

		// before starting the loop, define a support variable maxLength to keep track
		int maxLength = 1;

		for (int i = start + 1; i <= end; i++) { // loop starts from 2nd position
			if (sequence.get(i) > 0) { // exclude sequence ending with 0
				for (int j = start; j < i; j++) {
					if (sequence.get(j) > 0) {
						// check if appending current index to the previous subsequence: 1 current > previous ending, and size is increasing
						//if(timelist.get(i) >= timelist.get(j) && sizes[i] < sizes[j] +1){ // increasing sequence
						if ((sequence.get(i) + deltat) >= sequence.get(j) && sizes[i - start] < (sizes[j - start] + 1)) { // increasing band

							// if yes, update sizes and path
							sizes[i - start] = sizes[j - start] + 1;
							paths[i - start] = paths[j - start] + sequence.get(i) + " ";
							ArrayList<Integer> list = indexs.get(j - start);
							ArrayList<Integer> temp = new ArrayList<Integer>();
							temp.addAll(list);
							temp.add(temp.size(), i);
							indexs.remove(i - start);
							indexs.add(i - start, temp);
							//System.out.println("\t\t"+paths[i]+"\t"+indexs);

							// append current values to end
							// update maxLength if necessary
							if (maxLength < sizes[i - start])
								maxLength = sizes[i - start];
						}
					}

				}
			}


		}


		// count number of LIS
		int number = 0;
		for (int i = 0; i < n; i++) {
			if (sizes[i] == maxLength) {
				number += 1;
			}

		}

		//System.out.println("lis size : "+maxLength);

		// scan size array again to print out path when size matches MaxLength
		if (number > 0) {
			for (int i = 0; i < n; i++) {
				if (sizes[i] == maxLength) {
					lis = indexs.get(i);
				}

			}
		}

		//System.out.println("lis: "+lis+"\t"+lis.size());
		return lis;

	}

	public ArrayList<Integer> findLDB(ArrayList<Integer> oppo_sequence, int deltat) {
		ArrayList<Integer> indexs = new ArrayList<Integer>();

		//System.out.println("\t find longest Decreasing band");
		// get new piece
		int size = oppo_sequence.size() - 1;
		Piece new_piece = new Piece();
		int new_start = size - end;
		int new_end = size - start;
		new_piece.setStart(new_start);
		new_piece.setEnd(new_end);
		//System.out.println("\t\t new piece: ["+new_start+","+new_end+"]"+"\t"+size);

		// get lib in the new piece
		ArrayList<Integer> new_indexs = new ArrayList<Integer>();
		new_indexs = new_piece.findLIB(oppo_sequence, deltat);
		//System.out.println("\t\t\t new lib: "+new_indexs);

		// transfer into LDB in the original piece
		for (int i = new_indexs.size() - 1; i >= 0; i--) {
			int index = new_indexs.get(i);
			indexs.add(indexs.size(), size - index);
		}
		//System.out.println("\t\t LDB: "+indexs);

		return indexs;
	}

	/*
	 * find LMBs in a sequence of tuples, each of which represents vector of
	 * multiple attribute values: (v1, v2, ..., v_m)
	 */
	public ArrayList<Integer> findLMBWithMultipleAttributes(ArrayList<Integer> sequence, double deltat)
			throws Exception {

		ArrayList<Integer> incList = new ArrayList<Integer>();
		ArrayList<Integer> decList = new ArrayList<Integer>();

		LinkedHashMap<Integer, ArrayList> incPaths = new LinkedHashMap<Integer, ArrayList>();
		LinkedHashMap<Integer, ArrayList> decPaths = new LinkedHashMap<Integer, ArrayList>();

		int incLength = 0;
		int decLength = 0;

		for (int i = start; i <= end; i++) {
			int year = sequence.get(i);
			// System.out.println("\t year: " + i + "\t" + year);

			if (year > 0) { // skip missing vlaue

				int index_1 = getIncPositionOfTuple(incList, year, 0);
				int index_2 = getIncPositionOfTuple(incList, year, deltat);

				int index_3 = getDecPositionofTuple(decList, year, 0);
				int index_4 = getDecPositionofTuple(decList, year, deltat);

				// int index_11 = getIncPosition(incList, year);
				// int index_21 = getIncPosition(incList, year + deltat);

				/*
				 * if (index_1 !=index_11){ System.out.println(year +
				 * " in incList position: " + index_1 + ", not " + index_11); }
				 */

				// int index_31 = getDecPosition(decList, year);
				// int index_41 = getDecPosition(decList, year - deltat);

				ArrayList<Integer> incPath = new ArrayList<Integer>();
				ArrayList<Integer> decPath = new ArrayList<Integer>();

				// System.out.println("\t\t increasing index \t" + index_1 +
				// "\t" + index_2);
				for (int k = index_2; k >= index_1; k--) {
					int pred = 0;
					if (k > 0)
						pred = incList.get(k - 1);

					if (k < incList.size())
						incList.set(k, maxTuple(year, pred));
					else
						incList.add(k, maxTuple(year, pred));
					// System.out.println("\t\t\t\t max of (" + year + " and " +
					// pred + ") = " + maxTuple(year, pred));
					// System.out.println("\t\t\t\t incList:( " + incList.size()
					// + " ) " + incList);

					incPath.add(incPath.size(), k);
				}
				incPaths.put(i, incPath);
				// System.out.println("\t\t\t" + incPath);

				// System.out.println("\t\t decreasing index \t" + index_3 +
				// "\t" + index_4);
				for (int k = index_4; k >= index_3; k--) {
					int pred = 2999;
					if (k > 0)
						pred = decList.get(k - 1);

					if (k < decList.size())
						decList.set(k, minTuple(year, pred));
					else
						decList.add(k, minTuple(year, pred));
					// System.out.println("\t\t\t\t min of (" + year + " and " +
					// pred + ") = " + minTuple(year, pred));
					// System.out.println("\t\t\t\t decList: ( " +
					// decList.size() + " ) " + decList);
					decPath.add(decPath.size(), k);
				}
				decPaths.put(i, decPath);
				// System.out.println("\t\t\t" + decPath);

				incLength = Math.max(incLength, incList.size());
				decLength = Math.max(decLength, decList.size());
			} else {
				ArrayList<Integer> path = new ArrayList<Integer>();
				path.add(path.size(), -1);

				incPaths.put(i, path);
				decPaths.put(i, path);
			}
		}

		ArrayList<Integer> lmb = new ArrayList<Integer>();

		if (incLength >= decLength) {
			// System.out.println("increasing length \t" + incLength);
			lmb = band(incPaths, incLength - 1);
			increase = true;
		} else {
			// System.out.println("decreasing length \t" + decLength);
			lmb = band(decPaths, decLength - 1);
			increase = false;
		}

		return lmb;
	}

	/**
	 * Find a LMB in sequence
	 *
	 * @param sequence
	 * @param deltat
	 * @return
	 */
	public ArrayList<Integer> findLMB(ArrayList<Integer> sequence, double deltat) {

		ArrayList<Integer> incList = new ArrayList<Integer>();
		ArrayList<Integer> decList = new ArrayList<Integer>();

		LinkedHashMap<Integer, ArrayList> incPaths = new LinkedHashMap<Integer, ArrayList>();
		LinkedHashMap<Integer, ArrayList> decPaths = new LinkedHashMap<Integer, ArrayList>();

		int incLength = 0;
		int decLength = 0;

		for (int i = start; i <= end; i++) {
			int year = sequence.get(i);

			if (year > 0) {

				int index_1 = getIncPosition(incList, year);
				int index_2 = getIncPosition(incList, year + deltat);

				int index_3 = getDecPosition(decList, year);
				int index_4 = getDecPosition(decList, year - deltat);


				ArrayList<Integer> incPath = new ArrayList<Integer>();
				ArrayList<Integer> decPath = new ArrayList<Integer>();

				// System.out.println("\t\t increasing index \t" + index_1 +
				// "\t" + index_2);
				for (int k = index_2; k >= index_1; k--) {
					int pred = 0;
					if (k > 0)
						pred = incList.get(k - 1);

					if (k < incList.size())
						incList.set(k, Math.max(year, pred));
					else
						incList.add(k, Math.max(year, pred));

					incPath.add(incPath.size(), k);
					// System.out.println("\t\t\t" + incList);
				}
				incPaths.put(i, incPath);

				// System.out.println("\t\t decreasing index \t" + index_3 +
				// "\t" + index_4);
				for (int k = index_4; k >= index_3; k--) {
					int pred = 2999;
					if (k > 0)
						pred = decList.get(k - 1);

					if (k < decList.size())
						decList.set(k, Math.min(year, pred));
					else
						decList.add(k, Math.min(year, pred));

					decPath.add(decPath.size(), k);
					// System.out.println("\t\t\t" + decList);
				}
				decPaths.put(i, decPath);

				incLength = Math.max(incLength, incList.size());
				decLength = Math.max(decLength, decList.size());
				//System.out.println(incList+"\t"+incLength);
				//System.out.println(decList+"\t"+decLength);
			} else {
				ArrayList<Integer> path = new ArrayList<Integer>();
				path.add(path.size(), -1);

				incPaths.put(i, path);
				decPaths.put(i, path);
			}
		}

		ArrayList<Integer> lmb = new ArrayList<Integer>();


		if (incLength >= decLength) {
			// System.out.println("increasing length \t" + incLength);
			lmb = band(incPaths, incLength - 1);
			increase = true;
		} else {
			lmb = band(decPaths, decLength - 1);
			increase = false;
		}

		return lmb;
	}

	/**
	 * Print the LMB of length k+1
	 *
	 * @param incPaths
	 * @param k
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList<Integer> band(LinkedHashMap<Integer, ArrayList> incPaths, int k) {
		ArrayList<Integer> lmb = new ArrayList<Integer>();
		for (int i = 0; i <= k; i++) {
			lmb.add(i, 0);
		}

		int i = k;
		int j = end;//incPaths.size()-1;

		while (i >= 0) {
			ArrayList path = incPaths.get(j);
			if (path.contains(i)) {
				lmb.set(i, j);
				i--;
			}
			j--;
		}
		//System.out.println("\t LMB \t"+lmb);
		return lmb;
	}

	/**
	 * Find the successor index of year in the decreasing list
	 *
	 * @param list
	 * @param year
	 * @return
	 */
	private int getDecPosition(ArrayList<Integer> list, double year) {
		int position = 0;
		int i = 0;
		Boolean stop = false;
		while (i < list.size() && !stop) {
			int temp = list.get(i);
			if (temp < year) {
				position = i;
				stop = true;
			}
			i++;
		}
		if (!stop)
			position = list.size();

		return position;
	}

	/**
	 * Find the successor index of year in the increasing list
	 *
	 * @param arrayList
	 * @param year
	 * @return
	 */
	private int getIncPosition(ArrayList<Integer> arrayList, double year) {
		// TODO Auto-generated method stub
		int position = 0;

//		List<Integer> list = arrayList;
//		int index = Collections.binarySearch();

		if (!arrayList.isEmpty()) {
			int i = 0;
			Boolean stop = false;
			while (i < arrayList.size() && !stop) {
				int temp = arrayList.get(i);
				if (temp > year) {
					position = i;
					stop = true;
				}
				i++;
			}

			if (!stop)
				position = arrayList.size();
		}

		return position;
	}

	/*
	 * Find the successor index of a tuple in the increasing sequence of tuples
	 */
	private int getIncPositionOfTuple(ArrayList<Integer> list, int year, double deltat) throws Exception {
		// TODO Auto-generated method stub
		int position = 0;

		if (!list.isEmpty()) {
			int i = 0;
			Boolean stop = false;
			while (i < list.size() && !stop) {
				int temp = list.get(i);
				// (temp > year + deltat)
				// System.out.println(
				// "\t\t\t distance(" + temp + " - " + year + ") \t = " +
				// distance(year, temp) + "> " + deltat);
				if (distance(year, temp) > deltat) {
					// if (temp - year > deltat) {
					position = i;
					stop = true;
				}
				i++;
			}

			if (!stop)
				position = list.size();
		}

		return position;
	}

	private int getDecPositionofTuple(ArrayList<Integer> list, int year, double deltat) throws Exception {
		int position = 0;
		int i = 0;
		Boolean stop = false;
		while (i < list.size() && !stop) {
			int temp = list.get(i);
			// (temp < year - deltat)
			if (distance(year, temp) < -deltat) {
				position = i;
				stop = true;
			}
			i++;
		}
		if (!stop)
			position = list.size();

		return position;
	}

	public ArrayList<Integer> findLIB(ArrayList<Integer> sequence, int deltat) {
		ArrayList<Integer> indexs = new ArrayList<Integer>();

		// n bandlist: for key i, store the increasing bands ending at i
		ArrayList<HashMap<Pipe, ArrayList>> bandlists = new ArrayList<HashMap<Pipe, ArrayList>>();
		//int n = timelist.size();
		for (int i = start; i <= end; i++) {
			int time = sequence.get(i);
			//System.out.println("\t"+i+"\t"+time);
			HashMap<Pipe, ArrayList> bandlist = new HashMap<Pipe, ArrayList>();

			Pipe pipe = new Pipe();
			pipe.setLower1(time - deltat);
			pipe.setLower2(time);
			pipe.setUpper1(time);
			pipe.setUpper2(time + deltat);
			pipe.setId(i);

			ArrayList band = new ArrayList();
			band.add(band.size(), pipe);

			bandlist.put(pipe, band);

			bandlists.add(bandlists.size(), bandlist);
		}

		int maxLength = 1;

		for (int i = start + 1; i <= end; i++) {
			if (sequence.get(i) > 0) {
				int time1 = sequence.get(i);
				//System.out.println("\t process "+i+"\t"+timelist.get(i));
				HashMap<Pipe, ArrayList> bandlist1 = bandlists.get(i - start);

				for (int j = start; j < i; j++) {
					if (sequence.get(j) > 0) {
						//	System.out.println("\t\t process "+j+"\t"+timelist.get(j));
						int time2 = sequence.get(j);

						if ((sequence.get(i) + deltat) >= sequence.get(j)) {
							HashMap<Pipe, ArrayList> bandlist2 = bandlists.get(j - start);
							for (Map.Entry<Pipe, ArrayList> e2 : bandlist2.entrySet()) {
								Pipe pipe2 = e2.getKey();
								ArrayList band2 = e2.getValue();

								Pipe pipe1 = new Pipe();
								pipe1.setId(i);
								if (pipe2.getLower1() > (time1 - deltat)) {
									pipe1.setLower1(pipe2.getLower1());
									pipe1.setLower2(time1);

									pipe1.setUpper1(pipe1.getLower1() + deltat);
									pipe1.setUpper2(pipe1.getLower2() + deltat);


								} else {
									pipe1.setLower1(time1 - deltat);
									pipe1.setLower2(time1);
									pipe1.setUpper1(time1);
									pipe1.setUpper2(time1 + deltat);

								}

								//System.out.println("\t\t\t add new node");
								//System.out.println("\t\t\t\t[("+pipe1.getLower1()+","+pipe1.getLower2()+"),("+pipe1.getUpper1()+","+pipe1.getUpper2()+")]");

								if (pipe1.getLower1() <= pipe1.getLower2() && pipe1.getUpper1() <= pipe1.getUpper2()) {
									ArrayList temp = new ArrayList();
									temp.addAll(band2);
									temp.add(temp.size(), pipe1);

									/*
									 * if size increases, replace band1 by band2 + pipe1
									 */
									Set<Pipe> keyset = bandlist1.keySet();
									Iterator<Pipe> it = keyset.iterator();
									//System.out.println("\t\t check bandlist1");
									boolean stop = false;
									while (it.hasNext() && !stop) {
										Pipe p1 = it.next();
										if ((p1.getLower1() == pipe1.getLower1()) && (p1.getLower2() == pipe1.getLower2()) && (p1.getUpper1() == pipe1.getUpper1()) && (p1.getUpper2() == pipe1.getUpper2())) {
											stop = true;
											//System.out.println("\t replace band1 w. band2");
											ArrayList band1 = bandlist1.get(p1);
											if (band2.size() + 1 > band1.size()) {
												bandlist1.put(p1, temp);
											}

										}

									}

									if (!stop)
										bandlist1.put(pipe1, temp);

									if (temp.size() > maxLength)
										maxLength = temp.size();
								}


							}
						}
					}
				}
			}
		}

		// print out pipe w. maxLength
		for (int j = 0; j < bandlists.size(); j++) {
			//System.out.println("\t position "+j);
			HashMap<Pipe, ArrayList> bandlist = bandlists.get(j);
			for (Map.Entry<Pipe, ArrayList> e : bandlist.entrySet()) {
				Pipe pipe = e.getKey();
				ArrayList band = e.getValue();
				if (band.size() == maxLength) {
					// indexlist to store record id in lib
					indexs = new ArrayList();
					for (int m = 0; m < band.size(); m++) {

						Pipe p = (Pipe) band.get(m);
						int id = p.getId();
						indexs.add(indexs.size(), id);
						//System.out.println("\t\t[("+p.getLower1()+","+p.getLower2()+"),("+p.getUpper1()+","+p.getUpper2()+")] \t"+p.getId());
					}
				}

			}
		}
		//System.out.println("\t maxlength: "+maxLength+"\t"+indexs);
		return indexs;
	}

	public ArrayList<Integer> findErrors(ArrayList<Integer> lmb, ArrayList<Integer> sequence) {
		// TODO Auto-generated method stub
		ArrayList<Integer> errors = new ArrayList<Integer>();

		//System.out.println("\t"+lib);

		for (int i = start; i <= end; i++) {
			int year = sequence.get(i);
			if (!lmb.contains(i) && year != 0)
				errors.add(errors.size(), i);
		}
		//System.out.println("errors: "+errors+"\t"+errors.size());
		return errors;
	}


	private int getMaxError(ArrayList<Integer> lmb, ArrayList<Integer> sequence) {
		// TODO Auto-generated method stub
		ArrayList<Integer> error = new ArrayList<Integer>();
		int max = 0;

		for (int i = start; i <= end; i++) {
			int year = sequence.get(i);
			if (!lmb.contains(i) && year != 0)
				error.add(error.size(), i);
		}

		int count = 1;

		for (int j = 0; j < error.size() - 1; j++) {
			int index = error.get(j);
			int next_index = error.get(j + 1);
			if ((index + 1) == next_index)
				count++;
			else
				count = 1;
			max = Math.max(max, count);
		}

		max = Math.max(max, count);

		if (error.size() == 0)
			max = 0;

		//System.out.println("Error: \t"+error+"\t LEB: \t"+max);

		return max;
	}

	public Double computeGain(ArrayList<Integer> sequence, double deltat) {
		// get LMB in sequence
		lmb = findLMB(sequence, deltat);

		//get outliers in sequence
		maxErrors = getMaxError(lmb, sequence);
		errors = findErrors(lmb, sequence);

		double gain = Math.pow(lmb.size(), 2) - Math.pow(errors.size(), 2);
		return gain;
	}

	public Double computeGainWithMultipleAttributes(ArrayList<Integer> sequence, double deltat) throws Exception {

		// get LMB in sequence
		lmb = findLMBWithMultipleAttributes(sequence, deltat);
		// lmb = findLMB(sequence, deltat);

		// get outliers in sequence
		maxErrors = getMaxError(lmb, sequence);
		errors = findErrors(lmb, sequence);

		// double gain = Math.pow(lmb.size(), 2) - Math.pow(maxErrors, 2);
		double gain = Math.pow(lmb.size(), 2) - Math.pow(errors.size(), 2);

		// System.out.println("\t\t pieces \t" + sequence.size() + "\t\t lmb \t"
		// + lmb.size() + "\t\t errors \t"
		// + errors.size() + "\t\t gain \t" + gain);
		// System.out.println();

		return gain;
	}

	public double computeSetGain(ArrayList<HashSet> set_sequence,
								 ArrayList<HashSet> oppo_set_sequence, int deltat) {
		// TODO Auto-generated method stub

		double gain = 0;
		//System.out.println(" compute gain");

		/*
		 * gain of increasing band
		 */
		int n = size;//getPieceSize(set_sequence);//end - start + 1;
		int[][] lib = findSetLIB(set_sequence, deltat);

		//printArray(lib);

		int[][] lib_errors = new int[n][2];
		lib_errors = findSetErrors(lib, set_sequence);
		//System.out.println("outliers: ");
		//printArray(lib_errors);

		int lib_max_error = findMax_set_errors(lib_errors);
		//System.out.println("max # clustered errors \t"+lib_max_error);

		double lib_gain = Math.pow(lib.length, 2) - Math.pow(lib_errors.length, 2);
		//System.out.println("lib gain: \t"+lib_gain+"\t"+lib_errors.length);
		int lib_error_size = n - lib.length;
		lib_gain = Math.pow(lib.length, 2) - Math.pow(lib_error_size, 2);
		//System.out.println("lib gain \t"+lib_gain+"\t"+lib_error_size);

		//System.out.println("n \t lib size \t error size \t error position size");
		//System.out.println(n+"\t"+lib.length+"\t"+lib_errors.length+"\t"+lib_error_size);

		/*
		 * gain of decreasing band
		 */
		int[][] ldb = findSetLDB(oppo_set_sequence, deltat);
		//System.out.println("longest decreasing band: ");
		//printArray(ldb);

		int[][] ldb_errors = findSetErrors(ldb, set_sequence);
		//System.out.println("outliers: ");
		//printArray(ldb_errors);

		int ldb_max_error = findMax_set_errors(ldb_errors);
		//System.out.println("max # clustered errors \t"+ldb_max_error);

		double ldb_gain = Math.pow(ldb.length, 2) - Math.pow(ldb_errors.length, 2);
		//System.out.println("decreasing gain \t"+ldb_gain+"\t"+ldb_errors.length);

		int ldb_error_size = n - ldb.length;
		ldb_gain = Math.pow(ldb.length, 2) - Math.pow(ldb_error_size, 2);
		//System.out.println("decreasing gain \t"+ldb_gain+"\t"+ldb_error_size);

		//System.out.println("n \t ldb size \t error size \t error position size");
		//System.out.println(n+"\t"+ldb.length+"\t"+ldb_errors.length+"\t"+ldb_error_size);

		gain = Math.max(lib_gain, ldb_gain);

		this.set_lib = lib;
		this.set_errors = lib_errors;
		this.maxErrors = lib_max_error;
		this.increase = true;

		if (lib_gain < ldb_gain) {
			this.set_lib = ldb;
			this.set_errors = ldb_errors;
			this.maxErrors = ldb_max_error;
			this.increase = false;
		}

		//System.out.println("gain \t"+gain);
		return gain;
	}

	private int[][] findSetLDB(ArrayList<HashSet> oppo_set_sequence, int deltat) {
		// TODO Auto-generated method stub

		//System.out.println("\t find longest Decreasing band");
		// get new piece
		int size = oppo_set_sequence.size() - 1;
		Piece new_piece = new Piece();
		int new_start = size - end;
		int new_end = size - start;
		new_piece.setStart(new_start);
		new_piece.setEnd(new_end);
		//System.out.println("\t\t new piece: ["+new_start+","+new_end+"]"+"\t"+size);

		// get lib in the new piece
		int[][] new_indexs = new_piece.findSetLIB(oppo_set_sequence, deltat);
		//System.out.println("\t\t\t new lib: "+new_indexs);

		int length = new_indexs.length;
		int[][] indexs = new int[length][2];
		// transfer into LDB in the original piece

		for (int i = new_indexs.length - 1; i >= 0; i--) {
			int id = new_indexs[i][0];
			int time = new_indexs[i][1];
			indexs[length - 1 - i][0] = size - id;
			indexs[length - 1 - i][1] = time;

		}
		//System.out.println("\t\t LDB: "+indexs);

		return indexs;
	}

	private int[][] findSetErrors(int[][] lib, ArrayList<HashSet> set_sequence) {
		// TODO Auto-generated method stub
		int n = end - start + 1;
		int[][] lib_errors = new int[n][2];
		int s = 0;

		//System.out.println("find outliers");
		for (int i = start; i <= end; i++) {
			HashSet years = set_sequence.get(i);
			Iterator it = years.iterator();
			//System.out.println("\t"+i+"\t"+years);
			while (it.hasNext()) {
				int year = (Integer) it.next();
				//System.out.println("\t\t"+year);
				if (year > 0) {
					// check if year is w.i. the band
					boolean outlier = true;
					int j = 0;
					while (j < lib.length && outlier) {
						int id = lib[j][0];
						int year2 = lib[j][1];
						if (i == id && year == year2)
							outlier = false;
						j++;
					}
					if (outlier) {
						lib_errors[s][0] = i;
						lib_errors[s][1] = year;
						s++;
					}
				}
			}
		}

		lib_errors = cleanArray(lib_errors);

		return lib_errors;
	}

	private int[][] cleanArray(int[][] lib_errors) {
		// TODO Auto-generated method stub
		int size = 0;

		for (int i = 0; i < lib_errors.length; i++) {
			int id = lib_errors[i][0];
			int time = lib_errors[i][1];
			if (!(id == 0 && time == 0))
				size++;
		}

		int[][] temp = new int[size][2];

		for (int j = 0; j < size; j++) {
			int id = lib_errors[j][0];
			int time = lib_errors[j][1];
			temp[j][0] = id;
			temp[j][1] = time;
		}

		return temp;
	}

	private void printArray(int[][] lib) {
		// TODO Auto-generated method stub
		System.out.println("lib size: " + lib.length);
		int i = 0;
		while (i < lib.length && lib[i] != null) {
			System.out.println("\t" + lib[i][0] + "\t" + lib[i][1]);
			i++;
		}
	}

	private int[][] findSetLIB(ArrayList<HashSet> set_sequence, int deltat) {
		// TODO Auto-generated method stub
		//int z = end - start + 1; 


		// n bandlist: for key i, store the increasing bands ending at i
		ArrayList<HashMap<Pipe, ArrayList>> bandlists = new ArrayList<HashMap<Pipe, ArrayList>>();
		//int n = timelist.size();
		for (int i = start; i <= end; i++) {
			HashSet years = set_sequence.get(i);
			HashMap<Pipe, ArrayList> bandlist = new HashMap<Pipe, ArrayList>();
			//System.out.println("\t"+i+"\t"+years);

			Iterator it = years.iterator();
			while (it.hasNext()) {
				int time = (Integer) it.next();
				//System.out.println("\t\t"+time);
				if (time > 0) {
					Pipe pipe = new Pipe();
					pipe.setLower1(time - deltat);
					pipe.setLower2(time);
					pipe.setUpper1(time);
					pipe.setUpper2(time + deltat);
					pipe.setId(i);
					pipe.setTime(time);
					ArrayList band = new ArrayList();
					band.add(band.size(), pipe);

					bandlist.put(pipe, band);
				}


			}
			//System.out.println("\t bandlist size : "+bandlist.size());
			bandlists.add(bandlists.size(), bandlist);
		}

		int maxLength = 1;

		for (int i = start + 1; i <= end; i++) {

			HashSet years1 = set_sequence.get(i);
			int min_year1 = getMinYear(years1);
			int max_year1 = getMaxYear(years1);

			if (max_year1 > 0) {
				Iterator it1 = years1.iterator();
				while (it1.hasNext()) {
					int time1 = (Integer) it1.next();
					if (time1 > 0) {
						//System.out.println("\t "+i+"\t"+time1);

						HashMap<Pipe, ArrayList> bandlist1 = bandlists.get(i - start);

						for (int j = start; j < i; j++) {

							HashSet years2 = set_sequence.get(j);
							int min_year2 = getMinYear(years2);
							int max_year2 = getMaxYear(years2);

							if (max_year2 > 0) {
								Iterator it2 = years2.iterator();
								while (it2.hasNext()) {
									int time2 = (Integer) it2.next();
									if (time2 > 0) {
										//System.out.println("\t\t process "+j+"\t"+time2);
										if ((time1 + deltat) >= time2) {
											HashMap<Pipe, ArrayList> bandlist2 = bandlists.get(j - start);
											for (Map.Entry<Pipe, ArrayList> e2 : bandlist2.entrySet()) {
												Pipe pipe2 = e2.getKey();
												ArrayList band2 = e2.getValue();
												int time = pipe2.getTime();
												if (time == time2) {
													Pipe pipe1 = new Pipe();
													pipe1.setId(i);
													pipe1.setTime(time1);
													if (pipe2.getLower1() > (time1 - deltat)) {
														pipe1.setLower1(pipe2.getLower1());
														pipe1.setLower2(time1);

														pipe1.setUpper1(pipe1.getLower1() + deltat);
														pipe1.setUpper2(pipe1.getLower2() + deltat);
													} else {
														pipe1.setLower1(time1 - deltat);
														pipe1.setLower2(time1);
														pipe1.setUpper1(time1);
														pipe1.setUpper2(time1 + deltat);
													}


													if (pipe1.getLower1() <= pipe1.getLower2() && pipe1.getUpper1() <= pipe1.getUpper2()) {
														ArrayList temp = new ArrayList();
														temp.addAll(band2);
														temp.add(temp.size(), pipe1);


														// if size increases, replace band1 by band2 + pipe1

														Set<Pipe> keyset = bandlist1.keySet();
														Iterator<Pipe> it = keyset.iterator();
														//System.out.println("\t\t check bandlist1");
														boolean stop = false;
														while (it.hasNext() && !stop) {
															Pipe p1 = it.next();
															int t1 = p1.getTime();
															if ((t1 == time1) && (p1.getLower1() == pipe1.getLower1()) && (p1.getLower2() == pipe1.getLower2()) && (p1.getUpper1() == pipe1.getUpper1()) && (p1.getUpper2() == pipe1.getUpper2())) {
																stop = true;
																//System.out.println("\t replace band1 w. band2");
																ArrayList band1 = bandlist1.get(p1);
																if (band2.size() + 1 > band1.size()) {
																	bandlist1.put(p1, temp);
																	//System.out.println("\t\t\t add new node");
																	//System.out.println("\t\t\t\t[("+pipe1.getLower1()+","+pipe1.getLower2()+"),("+pipe1.getUpper1()+","+pipe1.getUpper2()+")] \t"+temp.size());

																}

															}

														}

														if (!stop) {
															bandlist1.put(pipe1, temp);
															//System.out.println("\t\t\t add new node");
															//System.out.println("\t\t\t\t[("+pipe1.getLower1()+","+pipe1.getLower2()+"),("+pipe1.getUpper1()+","+pipe1.getUpper2()+")] \t"+temp.size());

														}

														if (temp.size() > maxLength)
															maxLength = temp.size();
													}
												}
											}
										}

									}
								}
							}
						}
					}

				}
			}
		}
		//System.out.println("\t maxlength: "+maxLength+"\t");
		// print out pipe w. maxLength
		int[][] indexs = new int[maxLength][2];

		int s = 0;
		for (int j = 0; j < bandlists.size(); j++) {
			//System.out.println("\t position "+j);
			HashMap<Pipe, ArrayList> bandlist = bandlists.get(j);
			for (Map.Entry<Pipe, ArrayList> e : bandlist.entrySet()) {
				Pipe pipe = e.getKey();
				ArrayList band = e.getValue();
				if (band.size() == maxLength) {
					// indexlist to store record id in lib
					indexs = new int[maxLength][2];
					s = 0;
					for (int m = 0; m < band.size(); m++) {

						Pipe p = (Pipe) band.get(m);
						int id = p.getId();
						int time = p.getTime();
						indexs[s][0] = id;
						indexs[s][1] = time;
						s++;
						//System.out.println("\t\t[("+p.getLower1()+","+p.getLower2()+"),("+p.getUpper1()+","+p.getUpper2()+")] \t"+p.getId());
					}
				}

			}
		}
		//System.out.println("\t maxlength: "+maxLength+"\t"+indexs);


		return indexs;
	}

	public int getPieceSize(ArrayList<HashSet> set_sequence) {
		// TODO Auto-generated method stub
		int size = 0;
		//System.out.println("check for piece size");
		for (int i = start; i <= end; i++) {
			HashSet years = set_sequence.get(i);

			int max_year = getMaxYear(years);
			if (max_year > 0)
				size++;
			//System.out.println("\t"+i+"\t"+years+"\t"+size);
		}
		return size;
	}


	public ArrayList<Piece> splitPiece(ArrayList<HashSet> set_sequence) {
		// TODO Auto-generated method stub
		ArrayList<Piece> temp = new ArrayList<Piece>();
		int previousMaxYear = 0;
		int previousMinYear = 2999;
		HashSet previousYears = new HashSet();
		int previousIndex = -1;

		Piece piece = new Piece();
		piece.setStart(start);
		piece.setIncrease(!increase);

		for (int i = start; i <= end; i++) {
			HashSet years = set_sequence.get(i);
			int maxYear = getMaxYear(years);
			int minYear = getMinYear(years);

			if (maxYear > 0) {
				if (previousMaxYear > 0) {
					if (increase) { // split piece into non-increasing pieces
						//System.out.println("\t\t\t\t\t"+i+"\t"+years);
						if (previousMaxYear < minYear) {
							piece.setEnd(previousIndex);
							int size = piece.getPieceSize(set_sequence);
							piece.setSize(size);
							temp.add(temp.size(), piece);
							//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

							piece = new Piece();
							piece.setStart(i);
							piece.setIncrease(!increase);


						}
					} else { // split piece into non-decreasing pieces
						if (previousMinYear > maxYear) {
							piece.setEnd(previousIndex);
							int size = piece.getPieceSize(set_sequence);
							piece.setSize(size);
							temp.add(temp.size(), piece);
							//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

							piece = new Piece();
							piece.setStart(i);
							piece.setIncrease(!increase);
						}
					}
				}

				previousMaxYear = maxYear;
				previousMinYear = minYear;
				previousYears = years;
				previousIndex = i;
			}


		}

		piece.setEnd(end);
		piece.setIncrease(!increase);
		int size = piece.getPieceSize(set_sequence);
		piece.setSize(size);
		//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

		temp.add(temp.size(), piece);

		System.out.println("print temp pieces \t" + temp.size());
		printPiece(temp, set_sequence);
		return temp;
	}

	private void printPiece(ArrayList<Piece> pieces,
							ArrayList<HashSet> set_sequence) {
		// TODO Auto-generated method stub
		for (int i = 0; i < pieces.size(); i++) {
			Piece piece = pieces.get(i);
			System.out.println("\t\t piece \t [" + piece.getStart() + ", " + piece.getEnd() + "] \t" + piece.size + "\t" + piece.isIncrease());
			for (int j = piece.getStart(); j <= piece.getEnd(); j++) {
				HashSet years = set_sequence.get(j);
				System.out.println("\t\t\t" + j + "\t" + years);

			}
		}
	}


	/**
	 * Find possible values for missing and erroneous values in a series.
	 *
	 * @param sequence
	 * @param deltat
	 * @param nextPiece
	 * @param prePiece
	 * @param repairs2
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected HashMap<Integer, Triple> repairPiece(ArrayList<Integer> sequence, int deltat, HashMap<Integer, Triple> repairs, Piece prePiece, Piece nextPiece) {

		for (int j = start; j <= end; j++) {
			if (!lmb.contains(j)) {
				Triple triple = new Triple();
				//System.out.println("\t error \t"+j+"\t"+sequence.get(j));
				//linearRepairTriple(triple, sequence, j, prePiece, nextPiece);
				repairTriple(triple, sequence, j, deltat, prePiece, nextPiece);
				repairs.put(j, triple);
			}
		}

		return repairs;
	}

	/**
	 * Repair by [previous_value, next_value] of the LMB, and set avg by AVG(min, max).
	 *
	 * @param triple
	 * @param sequence
	 * @param j
	 * @param nextPiece
	 * @param prePiece
	 */
	@SuppressWarnings("rawtypes")
	void linearRepairTriple(Triple triple, ArrayList<Integer> sequence, int j, Piece prePiece, Piece nextPiece) {
		int max = 0;
		int min = 2999;

		//System.out.println("lmb \t"+lmb);

		int nextIndex = getIncPosition(lmb, j);
		if (nextPiece != null) {
			int nextKey = 0;
			if (nextIndex < lmb.size())
				nextKey = lmb.get(nextIndex);
				//System.out.println("\t"+j+"\t next index \t"+lmb.get(nextIndex)+"\t"+sequence.get(key));			

			else
				//System.out.println("\t"+j+"\t is last");
				nextKey = nextPiece.lmb.get(0);
			max = Math.max(max, sequence.get(nextKey));
			min = Math.min(min, sequence.get(nextKey));
		} else
			max = 2999;


		int preIndex = nextIndex - 1;//getDecPosition(lmb, j);
		if (prePiece != null) {
			int preKey = 0;
			if (preIndex < lmb.size() && preIndex >= 0)
				preKey = lmb.get(preIndex);
				//System.out.println("\t"+j+"\t previous index \t"+lmb.get(preIndex)+"\t"+sequence.get(key));			
			else
				preKey = prePiece.lmb.get(prePiece.lmb.size() - 1);
			//System.out.println("\t"+j+"\t is first");
			max = Math.max(max, sequence.get(preKey));
			min = Math.min(min, sequence.get(preKey));
		} else
			min = 0;

		//System.out.println("\t"+j+"\t repaired value  \t ["+min+", "+max+"]");			
		triple.setStart(min);
		triple.setEnd(max);

		setAverage(triple, max, min);
		System.out.println("avg \t" + min + "\t" + max + "\t" + triple.getAvg());


	}

	@SuppressWarnings("rawtypes")
	private void setAverage(Triple triple, int max, int min) {
		int avg = 0;
		if (min < 2500 && max > 500)
			avg = (min + max) / 2;
		else if (max <= 500)
			avg = min;
		else if (min >= 2500)
			avg = max;
		
		/*if(max < 2500 && min > 500)
			avg = (min + max) / 2;
		else if(min <= 500)
			avg = max;
		else if(max >= 2500)
			avg = min;*/
		triple.setAvg(avg);
	}

	/**
	 * Repair value with [previous_max - Delta t, next_min + Delta t]
	 *
	 * @param triple
	 * @param sequence
	 * @param j
	 * @param deltat
	 * @param nextPiece
	 * @param prePiece
	 */
	@SuppressWarnings("rawtypes")
	private void repairTriple(Triple triple, ArrayList<Integer> sequence, int j, int deltat, Piece prePiece, Piece nextPiece) {
		int max = 0;
		int min = 2999;

		if (increase) {

			for (int i = 0; i < lmb.size(); i++) {
				int index = lmb.get(i);
				if (index < j)
					max = Math.max(max, sequence.get(index));
				if (index > j)
					min = Math.min(min, sequence.get(index));
			}
		} else {
			for (int i = 0; i < lmb.size(); i++) {
				int index = lmb.get(i);
				if (index < j)
					min = Math.min(min, sequence.get(index));
				if (index > j)
					max = Math.max(max, sequence.get(index));
			}
		}
		//System.out.println("\t\t"+j+": \t ["+(max - deltat)+", "+(min + deltat)+"]");

		triple.setStart(max - deltat);
		triple.setEnd(min + deltat);

		setAverage(triple, max - deltat, min + deltat);
	}

}
