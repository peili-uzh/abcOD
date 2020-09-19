package org.music.missingtime;

import org.music.data.ReleaseLabel;

import java.util.*;

public class Block {

	public Block() {
	}

	;

	public ArrayList<Integer> sequence;
	public ArrayList<Piece> pieces;
	public ArrayList<Integer> oppo_sequence;

	public ArrayList<HashSet> set_sequence;
	public ArrayList<HashSet> oppo_set_sequence;
	public ArrayList<String> cata_sequence;
	public ArrayList<ArrayList> choppings;
	public ArrayList<Double> gains;
	public int deltat;
	public HashMap<Integer, Integer> truths;

	/*
	 * Parameters for multiple year attributes
	 */
	// public ArrayList<Year> year_sequence;
	// public ArrayList<Year> oppo_year_sequence;

	public void printPieces() {
		int i = 0;
		for (i = 0; i < pieces.size(); i++) {
			Piece piece = pieces.get(i);
			System.out.println(piece.start + " to " + piece.end);
		}
	}

	public HashMap<Integer, Integer> getGround_truths() {
		return truths;
	}

	public void setGround_truths(HashMap<Integer, Integer> ground_truths) {
		this.truths = ground_truths;
	}

	public Block(LinkedHashMap<Integer, Integer> timelist) throws Exception {

		ArrayList sequence = new ArrayList();
		ArrayList oppo_sequence = new ArrayList();
		// ArrayList<Year> years = new ArrayList<Year>();
		// ArrayList<Year> oppo_years = new ArrayList<Year>();

		for (int i = 0; i < timelist.size(); i++) {
			int time = timelist.get(i);
			// Year year = new Year(time);
			sequence.add(i, time);
			// years.add(i, year);
		}

		for (int j = timelist.size() - 1; j >= 0; j--) {
			int time = timelist.get(j);
			// Year year = new Year(time);
			oppo_sequence.add(oppo_sequence.size(), time);
			// oppo_years.add(oppo_years.size(), year);
		}

		this.sequence = sequence;
		this.oppo_sequence = oppo_sequence;

		/*
		 * sequence of multiple year attributes
		 */
		// year_sequence = years;
		// oppo_year_sequence = oppo_years;
	}

	public Block(LinkedHashMap<Integer, Integer> timelist, LinkedHashMap<Integer, String> catalist) {
		// TODO Auto-generated constructor stub
		ArrayList<HashSet> set_sequence = new ArrayList<HashSet>();
		ArrayList<HashSet> oppo_set_sequence = new ArrayList<HashSet>();
		ArrayList<String> cata_sequence = new ArrayList<String>();

		HashSet<Integer> set = new HashSet<Integer>();
		String current_cata = "";

		for (int i = 0; i < timelist.size(); i++) {
			int time = timelist.get(i);
			String cata = catalist.get(i);


			if (!cata.equals("")) {

				if (!cata.equals(current_cata)) {
					if (!set.isEmpty()) {
						set_sequence.add(set_sequence.size(), set);
						cata_sequence.add(cata_sequence.size(), current_cata);
						//System.out.println(current_cata+":\t"+set);
					}

					set = new HashSet<Integer>();
					current_cata = cata;


				}
				set.add(time);
			}

			//System.out.println(i+" proceed \t"+time+"\t"+cata);
		}

		if (!set.isEmpty()) {
			set_sequence.add(set_sequence.size(), set);
			cata_sequence.add(cata_sequence.size(), current_cata);
			//System.out.println(current_cata+":\t"+set);
			set = new HashSet<Integer>();

		}
		System.out.println("# of sets " + set_sequence.size());

		/*
		 * proceed in decreasing order
		 */
		for (int j = timelist.size() - 1; j >= 0; j--) {
			int time = timelist.get(j);
			String cata = catalist.get(j);

			if (!cata.equals("")) {

				if (!cata.equals(current_cata)) {
					if (!set.isEmpty()) {
						oppo_set_sequence.add(oppo_set_sequence.size(), set);
						//System.out.println(current_cata+":\t"+set);
					}

					set = new HashSet<Integer>();
					current_cata = cata;
				}
				set.add(time);

			}

			//System.out.println(j+" proceed \t"+time+"\t"+cata);
		}

		if (!set.isEmpty()) {
			oppo_set_sequence.add(oppo_set_sequence.size(), set);
		}
		System.out.println("# of oppo_sets " + oppo_set_sequence.size());

		System.out.println("# of cata_sequence " + cata_sequence.size());

		this.cata_sequence = cata_sequence;
		this.set_sequence = set_sequence;
		this.oppo_set_sequence = oppo_set_sequence;
	}


	public int getDeltat() {
		return deltat;
	}

	public void setDeltat(int deltat) {
		this.deltat = deltat;
	}

	public ArrayList<Piece> piece(ArrayList<Integer> temp) {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int j = 0; j < temp.size() - 1; j++) {
			int start = temp.get(j) + 1;
			int end = temp.get(j + 1);

			Piece piece = new Piece();
			piece.setStart(start);
			piece.setEnd(end);

			pieces.add(pieces.size(), piece);
			//System.out.println("\t"+start+" - "+end);
		}


		return pieces;
	}

	public ArrayList<Piece> set_piece(ArrayList<Integer> temp) {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int j = 0; j < temp.size() - 1; j++) {
			int start = temp.get(j) + 1;
			int end = temp.get(j + 1);

			Piece piece = new SetPiece();
			piece.setStart(start);
			piece.setEnd(end);

			// get the non_zero records in a piece
			int size = piece.getPieceSize(set_sequence);
			piece.setSize(size);

			pieces.add(pieces.size(), piece);
			//System.out.println("\t"+start+" - "+end+"\t"+size);
		}


		return pieces;
	}

	public ArrayList<Piece> findIncreasingPieces(int deltat) {
		// TODO Auto-generated method stub
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		ArrayList<Integer> temp = new ArrayList<Integer>();


		int previousYear = 0;
		int previousIndex = -1;
		temp.add(temp.size(), previousIndex);


		for (int i = 0; i < sequence.size(); i++) {
			int year = sequence.get(i);
			if (year != 0) {
				if (previousYear != 0) {
					int distance = year + deltat - previousYear;

					if (distance < 0)
						temp.add(temp.size(), previousIndex);
				}
				previousYear = year;
				previousIndex = i;
			}
		}

		temp.add(temp.size(), sequence.size() - 1);


		pieces = piece(temp);

		return pieces;
	}

	public ArrayList<Piece> findLeftSetPieces(int deltat) {
		// TODO Auto-generated method stub
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		ArrayList<Integer> temp = new ArrayList<Integer>();

		int previousMaxYear = 1;
		int previousMinYear = 3000;
		HashSet previousYears = new HashSet();
		int previousIndex = -1;
		temp.add(temp.size(), previousIndex);

		boolean increase = true;
		boolean first = true;

		Piece piece = new Piece();
		piece.setStart(0);


		//int size = 0; // # records in the piece 

		for (int i = 0; i < set_sequence.size(); i++) {
			HashSet years = set_sequence.get(i);
			int max_year = getMaxYear(years);
			int min_year = getMinYear(years);
			//System.out.println(i+"\t"+years+"\t"+max_year+"\t"+min_year);
			//System.out.println("\t\t"+previousMaxYear+"\t"+min_year);
			if (max_year > 0) {
				if (previousMaxYear > 0) {
					if (first) {
						if (!years.equals(previousYears)) {

							first = false;

							if (previousMaxYear <= min_year) {
								increase = true;

							} else if (previousMinYear >= max_year)
								increase = false;
							else {
								//System.out.println("\t cutoff :"+previousIndex);
								temp.add(temp.size(), previousIndex);
								first = true;

								piece.setEnd(previousIndex);
								piece.setIncrease(increase);
								int size = piece.getPieceSize(set_sequence);
								piece.setSize(size);
								pieces.add(pieces.size(), piece);
								//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

								piece = new Piece();
								piece.setStart(i);
							}
						}
					} else if ((increase && !(previousMaxYear <= min_year)) || (!increase && !(previousMinYear >= max_year))) {
						//System.out.println("\t cutoff :"+previousIndex);
						temp.add(temp.size(), previousIndex);
						first = true;

						piece.setEnd(previousIndex);
						piece.setIncrease(increase);
						int size = piece.getPieceSize(set_sequence);
						piece.setSize(size);
						pieces.add(pieces.size(), piece);
						//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

						piece = new Piece();
						piece.setStart(i);

					}
				}

				//System.out.println(i+"\t"+years+"\t"+increase+"\t"+first);
				previousMaxYear = max_year;
				previousMinYear = min_year;
				previousYears = years;
				previousIndex = i;

			}
		}

		piece.setEnd(set_sequence.size() - 1);
		piece.setIncrease(increase);
		int size = piece.getPieceSize(set_sequence);
		piece.setSize(size);
		pieces.add(pieces.size(), piece);
		//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

		temp.add(temp.size(), set_sequence.size() - 1);
		//System.out.println("initial piece");
		//pieces = set_piece(temp);

		// separate border nodes
		ArrayList<Piece> new_pieces = new ArrayList<Piece>();
		new_pieces = separateBorder(pieces);

		printPiece(new_pieces);

		this.pieces = new_pieces;

		return new_pieces;
	}

	public ArrayList<Piece> findSetPieces(int deltat) {
		// TODO Auto-generated method stub
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		ArrayList<Integer> temp = new ArrayList<Integer>();

		int previousMaxYear = 1;
		int previousMinYear = 3000;
		HashSet previousYears = new HashSet();
		int previousIndex = -1;
		temp.add(temp.size(), previousIndex);

		boolean increase = true;
		boolean first = true;

		Piece piece = new Piece();
		piece.setStart(0);


		//int size = 0; // # records in the piece 

		for (int i = 0; i < set_sequence.size(); i++) {
			HashSet years = set_sequence.get(i);
			int max_year = getMaxYear(years);
			int min_year = getMinYear(years);
			//System.out.println(i+"\t"+years+"\t"+max_year+"\t"+min_year);
			//System.out.println("\t\t"+previousMaxYear+"\t"+min_year);
			if (max_year > 0) {
				if (previousMaxYear > 0) {
					if (first) {
						if (!years.equals(previousYears)) {

							first = false;

							if (previousMaxYear <= min_year) {
								increase = true;

							} else if (previousMinYear >= max_year)
								increase = false;
							else {
								//System.out.println("\t cutoff :"+previousIndex);
								temp.add(temp.size(), previousIndex);
								first = true;

								piece.setEnd(previousIndex);
								piece.setIncrease(increase);
								int size = piece.getPieceSize(set_sequence);
								piece.setSize(size);
								pieces.add(pieces.size(), piece);
								//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

								piece = new Piece();
								piece.setStart(i);
							}
						}
					} else if ((increase && !(previousMaxYear <= min_year)) || (!increase && !(previousMinYear >= max_year))) {
						//System.out.println("\t cutoff :"+previousIndex);
						temp.add(temp.size(), previousIndex);
						first = true;

						piece.setEnd(previousIndex);
						piece.setIncrease(increase);
						int size = piece.getPieceSize(set_sequence);
						piece.setSize(size);
						pieces.add(pieces.size(), piece);
						//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

						piece = new Piece();
						piece.setStart(i);

					}
				}

				//System.out.println(i+"\t"+years+"\t"+increase+"\t"+first);
				previousMaxYear = max_year;
				previousMinYear = min_year;
				previousYears = years;
				previousIndex = i;

			}
		}

		piece.setEnd(set_sequence.size() - 1);
		piece.setIncrease(increase);
		int size = piece.getPieceSize(set_sequence);
		piece.setSize(size);
		pieces.add(pieces.size(), piece);
		//System.out.println("\t new piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());

		temp.add(temp.size(), set_sequence.size() - 1);
		//System.out.println("initial piece");
		//pieces = set_piece(temp);

		// separate border nodes
		ArrayList<Piece> new_pieces = new ArrayList<Piece>();
		new_pieces = separateBorder(pieces);

		//printPiece(new_pieces);

		this.pieces = new_pieces;

		return new_pieces;
	}

	protected void printPiece(ArrayList<Piece> pieces) {
		// TODO Auto-generated method stub
		for (int i = 0; i < pieces.size(); i++) {
			Piece piece = pieces.get(i);
			System.out.println("\t\t piece \t [" + piece.getStart() + ", " + piece.getEnd() + "] \t" + piece.size + "\t");
			for (int j = piece.getStart(); j <= piece.getEnd(); j++) {
				//HashSet years = set_sequence.get(j);
				System.out.println("\t\t\t" + j + "\t" + sequence.get(j));

			}
		}

	}

	private ArrayList<Piece> separateBorder(ArrayList<Piece> pieces) {
		// TODO Auto-generated method stub
		//System.out.println("separate border nodes in a piece: ");
		ArrayList<Piece> new_pieces = new ArrayList<Piece>();
		for (int i = 0; i < pieces.size() - 1; i++) {
			Piece piece = pieces.get(i);
			Piece next_piece = pieces.get(i + 1);
			if (piece.isIncrease() == next_piece.isIncrease()) {
				new_pieces.add(new_pieces.size(), piece);
				//System.out.println("\t add piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());
			} else {
				//System.out.println("\t separate piece \t ["+piece.getStart()+", "+piece.getEnd()+"] \t"+piece.size+"\t"+piece.isIncrease());
				int j = piece.getEnd();
				boolean stop = false;
				int sep = -1;
				while (j > piece.getStart() && !stop) {
					HashSet years = set_sequence.get(j);
					//System.out.println("\t\t"+j+"\t"+years);
					HashSet years2 = set_sequence.get(j - 1);
					if (!years.equals(years2)) {
						//System.out.println("\t separet at position "+j);
						sep = j;
						stop = true;
					}
					j--;
				}
				/*
				 * separate original piece into [start, sep-1], [sep, end]
				 */
				if (sep > -1) {
					Piece piece1 = new Piece();
					piece1.setStart(piece.getStart());
					piece1.setEnd(sep - 1);
					piece1.setIncrease(piece.isIncrease());
					int size1 = piece1.getPieceSize(set_sequence);
					piece1.setSize(size1);
					new_pieces.add(new_pieces.size(), piece1);
					//System.out.println("\t\t separate into new piece \t ["+piece1.getStart()+", "+piece1.getEnd()+"] \t"+piece1.size+"\t"+piece1.isIncrease());

					Piece piece2 = new Piece();
					piece2.setStart(sep);
					piece2.setEnd(piece.getEnd());
					piece2.setIncrease(piece.isIncrease());
					int size2 = piece2.getPieceSize(set_sequence);
					piece2.setSize(size2);
					new_pieces.add(new_pieces.size(), piece2);
					//System.out.println("\t\t separate into new piece \t ["+piece2.getStart()+", "+piece2.getEnd()+"] \t"+piece2.size+"\t"+piece2.isIncrease());
				}
			}
		}

		// add the last piece into new pieces
		Piece last_piece = pieces.get(pieces.size() - 1);
		new_pieces.add(new_pieces.size(), last_piece);
		return new_pieces;
	}

	protected int getMinYear(HashSet years) {
		// TODO Auto-generated method stub
		int min = 3000;

		Iterator it = years.iterator();
		while (it.hasNext()) {
			int year = (Integer) it.next();
			min = Math.min(min, year);
		}
		return min;
	}


	public ArrayList<ArrayList> getChoppings() {
		return choppings;
	}

	public void setChoppings(ArrayList<ArrayList> choppings) {
		this.choppings = choppings;
	}

	public ArrayList<Double> getGains() {
		return gains;
	}

	public void setGains(ArrayList<Double> gains) {
		this.gains = gains;
	}

	protected int getMaxYear(@SuppressWarnings("rawtypes") HashSet years) {
		int max = 0;

		@SuppressWarnings("rawtypes")
		Iterator it = years.iterator();
		while (it.hasNext()) {
			int year = (Integer) it.next();
			max = Math.max(max, year);
		}

		return max;
	}

	public ArrayList<Piece> withoutPieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int i = 0; i < sequence.size(); i++) {
			int start = i;// sequence.get(i);
			addPiece(pieces, start, start);
		}
		return pieces;
	}

	/**
	 * Find both increasing and decreasing pieces according to LCMBs.
	 *
	 * @param deltat
	 * @return
	 */
	public ArrayList<Piece> findLCMBPieces(double deltat) {

		ArrayList<Piece> pieces = new ArrayList<Piece>();

		//int max = 0; // max length of LCMBs
		HashMap<Integer, Integer> incBand = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> decBand = new HashMap<Integer, Integer>();
		incBand.put(0, 0);
		decBand.put(3000, 0);

		int incMax = getMax(incBand);
		int decMax = getMax(decBand);

		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < sequence.size(); i++) {
			int year = sequence.get(i);
			//System.out.println(i+"\t"+year);

			if (year > 0) {

				HashMap<Integer, Integer> incTemp = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> decTemp = new HashMap<Integer, Integer>();
				updateIfMax(incTemp, year, 1);
				updateIfMax(decTemp, year, 1);

				incMax = getMax(incBand);
				decMax = getMax(decBand);

				// update INC
				for (Map.Entry<Integer, Integer> e : incBand.entrySet()) {
					int key = e.getKey();
					int count = e.getValue();

					if (year + deltat >= key) {
						int newKey = Math.max(key, year);
						int newCount = count + 1;
						updateIfMax(incTemp, newKey, newCount);

					} else {
						if (count == incMax && count > decMax) {
							insertList(list, i);
							insertList(list, i - count);
						}
					}
				}
				incBand.clear();
				incBand.putAll(incTemp);

				//System.out.println("\t incBand \t"+incBand);
				//System.out.println("\t list \t"+list);

				// update DEC
				for (Map.Entry<Integer, Integer> e : decBand.entrySet()) {
					int key = e.getKey();
					int count = e.getValue();

					if (year - deltat <= key) {
						int newKey = Math.min(key, year);
						int newCount = count + 1;
						updateIfMax(decTemp, newKey, newCount);

					} else {
						if (count > incMax && count == decMax) {
							insertList(list, i);
							insertList(list, i - count);
						}
					}
				}
				decBand.clear();
				decBand.putAll(decTemp);

				//System.out.println("\t decBand \t"+decBand);
				//System.out.println("\t list \t"+list);

			} else {
				increaseMap(incBand);
				increaseMap(decBand);
			}

			//System.out.println("\t incList \t"+incBand);
			//System.out.println("\t decList \t"+decBand);
		}

		incMax = getMax(incBand);
		decMax = getMax(decBand);

		// insert INC
		for (Map.Entry<Integer, Integer> e : incBand.entrySet()) {
			//int key = e.getKey();
			int count = e.getValue();
			if (count == incMax && count > decMax) {
				insertList(list, sequence.size());
				insertList(list, sequence.size() - count);
			}
		}
		//System.out.println("\t list \t"+list);

		// insert DEC
		for (Map.Entry<Integer, Integer> e : decBand.entrySet()) {
			//int key = e.getKey();
			int count = e.getValue();
			if (count > incMax && count == decMax) {
				insertList(list, sequence.size());
				insertList(list, sequence.size() - count);
			}
		}
		//System.out.println("\t list \t"+list);

		for (int i = 0; i < list.size() - 1; i++) {
			int start = list.get(i);
			int end = list.get(i + 1) - 1;
			if (end >= start) {
				//System.out.println(i+"\t piece: ["+start+", "+end+"]");
				addPiece(pieces, start, end);
			}
		}

		//addPiece(pieces, 0, sequence.size()-1);

		return pieces;
	}

	public ArrayList<Piece> findLCMBPiecesWithMultipleAttributes(double deltat) throws Exception {

		ArrayList<Piece> pieces = new ArrayList<Piece>();

		// int max = 0; // max length of LCMBs
		HashMap<Integer, Integer> incBand = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> decBand = new HashMap<Integer, Integer>();
		incBand.put(0, 0);
		decBand.put(3000, 0);

		int incMax = getMax(incBand);
		int decMax = getMax(decBand);

		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < sequence.size(); i++) {
			int yearValue = sequence.get(i);

			if (yearValue > 0) { // Skip missing year values.

				HashMap<Integer, Integer> incTemp = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> decTemp = new HashMap<Integer, Integer>();
				updateIfMax(incTemp, yearValue, 1);
				updateIfMax(decTemp, yearValue, 1);

				incMax = getMax(incBand);
				decMax = getMax(decBand);

				// update INC
				for (Map.Entry<Integer, Integer> e : incBand.entrySet()) {
					int key = e.getKey();
					int count = e.getValue();

					// if (yearValue + deltat >= key) {
					if (distance(key, yearValue) >= -deltat) {
						// Math.max(key,yearValue);
						int newKey = maxTuple(key, yearValue);
						int newCount = count + 1;
						updateIfMax(incTemp, newKey, newCount);

					} else {
						if (count == incMax && count > decMax) {
							insertList(list, i);
							insertList(list, i - count);
						}
					}
				}
				incBand.clear();
				incBand.putAll(incTemp);

				// System.out.println("\t incBand \t" + incBand);
				// System.out.println("\t list \t"+list);

				// update DEC
				for (Map.Entry<Integer, Integer> e : decBand.entrySet()) {
					int key = e.getKey();
					int count = e.getValue();

					// if (yearValue - deltat <= key) {
					if (distance(key, yearValue) <= deltat) {
						// Math.min(key, yearValue);
						int newKey = minTuple(key, yearValue);
						int newCount = count + 1;
						updateIfMax(decTemp, newKey, newCount);

					} else {
						if (count > incMax && count == decMax) {
							insertList(list, i);
							insertList(list, i - count);
						}
					}
				}
				decBand.clear();
				decBand.putAll(decTemp);

				// System.out.println("\t decBand \t"+decBand);
				// System.out.println("\t list \t"+list);

			} else {
				increaseMap(incBand);
				increaseMap(decBand);
			}

			// System.out.println("\t incList \t"+incBand);
			// System.out.println("\t decList \t"+decBand);
		}

		incMax = getMax(incBand);
		decMax = getMax(decBand);

		// insert INC
		for (Map.Entry<Integer, Integer> e : incBand.entrySet()) {
			// int key = e.getKey();
			int count = e.getValue();
			if (count == incMax && count > decMax) {
				insertList(list, sequence.size());
				insertList(list, sequence.size() - count);
			}
		}
		// System.out.println("\t list \t"+list);

		// insert DEC
		for (Map.Entry<Integer, Integer> e : decBand.entrySet()) {
			// int key = e.getKey();
			int count = e.getValue();
			if (count > incMax && count == decMax) {
				insertList(list, sequence.size());
				insertList(list, sequence.size() - count);
			}
		}
		// System.out.println("\t list \t"+list);

		for (int i = 0; i < list.size() - 1; i++) {
			int start = list.get(i);
			int end = list.get(i + 1) - 1;
			if (end >= start) {
				// System.out.println(i+"\t piece: ["+start+", "+end+"]");
				addPiece(pieces, start, end);
			}
		}

		return pieces;
	}

	/**
	 * Increase each value in the map by 1
	 *
	 * @param map
	 */
	private void increaseMap(HashMap<Integer, Integer> map) {
		for (Map.Entry<Integer, Integer> e : map.entrySet()) {
			int key = e.getKey();
			int count = e.getValue();
			map.put(key, count + 1);
		}
	}

	/**
	 * Insert i into sorted list, if not existed
	 *
	 * @param list
	 * @param i
	 */
	private void insertList(ArrayList<Integer> list, int i) {
		boolean stop = false;
		int index = 0;

		while (index < list.size() && !stop) {
			int temp = list.get(index);
			if (temp >= i) {
				stop = true;

				if (temp > i) {
					list.add(index, i);
				}
			}
			index++;
		}

		if (!stop)
			list.add(index, i);
	}

	/**
	 * Get the maximal value in a map
	 *
	 * @param map
	 * @return
	 */
	private int getMax(HashMap<Integer, Integer> map) {
		int max = 0;

		Iterator it = map.values().iterator();
		while (it.hasNext()) {
			max = Math.max(max, (Integer) it.next());
		}

		return max;
	}

	protected int maxTuple(Integer yearValue1, Integer yearValue2)
			throws Exception {
		int max = 0;
		double d = distance(yearValue1, yearValue2);
		// System.out.println("max of " + yearValue1 + " and " + yearValue2 + "
		// => " + d);
		if (d > 0) {
			max = yearValue2;
		} else {
			max = yearValue1;
		}

		return max;
	}

	protected int minTuple(Integer yearValue1, Integer yearValue2)
			throws Exception {
		int min = 3000;
		double d = distance(yearValue1, yearValue2);
		// System.out.println("min of " + yearValue1 + " and " + yearValue2 + "
		// => " + d);
		if (d > 0) {
			min = yearValue1;
		} else {
			min = yearValue2;
		}

		return min;
	}

	/*
	 * compute distance of two tuples x1, x2 by || x2 || - || x1 ||
	 */
	protected double distance(Integer yearValue1, Integer yearValue2)
			throws Exception {
		if (yearValue1 == 0) {
			return yearValue2;
		} else {
			if (yearValue2 == 0) {
				return -yearValue1;
			} else {
				if (yearValue1 == 2000 || yearValue2 == 2000) {
					return yearValue2 - yearValue1;
				} else {
					// Year year1 = new Year(yearValue1);
					// Year year2 = new Year(yearValue2);

					/*
					 * tuples of two dimensional vector
					 */
					// Integer century1 = yearValue1 / 100;
					// Integer decade1 = yearValue1 - century1 * 100;

					// System.out.println(yearValue1 + "\t" + century1 + "\t" +
					// decade1);

					// Integer century2 = yearValue2 / 100;
					// Integer decade2 = yearValue2 - century2 * 100;

					// double norm21 = Math.sqrt(Math.pow(century1, 2) +
					// Math.pow(decade1, 2));
					// double norm22 = Math.sqrt(Math.pow(century2, 2) +
					// Math.pow(decade2, 2));

					/*
					 * tuples of two dimensional vector
					 */
					// double norm21 = Math.sqrt(Math.pow(year1.centuries, 2) +
					// Math.pow(year1.decades, 2));
					// double norm22 = Math.sqrt(Math.pow(year2.centuries, 2) +
					// Math.pow(year2.decades, 2));

					/*
					 * tuples of four dimensional vector
					 */

					// Integer m1 = century1 / 10;
					// Integer c1 = century1 - m1 * 10;

					// Integer d1 = decade1 / 10;
					// Integer y1 = d1 - d1 * 10;

					// Integer m2 = century2 / 10;
					// Integer c2 = century2 - m2 * 10;

					// Integer d2 = decade2 / 10;
					// Integer y2 = d2 - d2 * 10;

					// double norm41 = Math.sqrt(Math.pow(m1, 2) + Math.pow(c1,
					// 2) + Math.pow(d1, 2) + Math.pow(y1, 2));
					// double norm42 = Math.sqrt(Math.pow(m2, 2) + Math.pow(c2,
					// 2) + Math.pow(d2, 2) + Math.pow(y2, 2));

					/*
					 * tuples of months and years
					 */
					// Integer year1 = yearValue1 / 100;
					// Integer month1 = yearValue1 - year1 * 100;

					// System.out.println(year1 + "\t" + year1 + "\t" + month1);
					// Integer year2 = yearValue2 / 100;
					// Integer month2 = yearValue2 - year1 * 100;

					// double norm21 = Math.sqrt(Math.pow(year1, 2) +
					// Math.pow(month1, 2));
					// double norm22 = Math.sqrt(Math.pow(year2, 2) +
					// Math.pow(month2, 2));

					double norm21 = Math.sqrt(Math.pow(yearValue1, 2) + Math.pow(yearValue1, 2));
					double norm22 = Math.sqrt(Math.pow(yearValue2, 2) + Math.pow(yearValue2, 2));
					// double d = yearValue2 - yearValue1;
					double d = norm22 - norm21;
					// double d = norm42 - norm41;

					// if (Math.abs(yearValue1 - yearValue2) <= 3.0 &&
					// yearValue1 >
					// 1999 && yearValue2 > 1999) {
					// System.out.println("distance of (" + yearValue1 + "(" +
					// norm21 + ")" + ", " + yearValue2 + "("
					// + norm22 + ")" + ") = " + d);
					// }

					if (yearValue2 > yearValue1 && d < 0) {
						return yearValue2 - yearValue1;
					} else {
						if (yearValue2 < yearValue1 && d > 0) {
							return yearValue2 - yearValue1;
						} else {
							return yearValue2 - yearValue1;
							// return d;
						}
					}

				}
			}
		}
	}

	private void updateIfMax(HashMap<Integer, Integer> map, int year, int count) {
		if (map.containsKey(year))
			count = Math.max(count, map.get(year));
		map.put(year, count);
	}

	public ArrayList<Piece> findPieces(int deltat) {
		/*
		 * find both increasing and decreasing pieces
		 */
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		int start = 0;

		int previousYear = 0;
		int previousIndex = -1;
		int previousDistance = 3000;
		int distance = 3000;


		for (int i = 0; i < sequence.size() - 1; i++) {
			int year = sequence.get(i);
			if (year != 0) {


				if (previousYear != 0) {

					distance = year - previousYear;
					
					/*
					if(Math.abs(distance)==deltat){
						distance = 0;
						//System.out.println("\t should merge \t"+year+"\t"+previousYear);
					}*/


					if (previousDistance != 3000) {
						//System.out.println(i+"\t"+previousIndex+"\t"+year+"\t"+previousYear+"\t"+distance+"\t"+previousDistance);
						if (previousDistance * distance != 0) {
							if (start <= previousIndex - 1)
								addPiece(pieces, start, previousIndex - 1);

							addPiece(pieces, previousIndex, i - 1);
							start = i;
						}
					}
				}


				if (distance != 0) {
					previousYear = year;
					previousIndex = i;
					previousDistance = distance;
				}

			}
		}

		addPiece(pieces, start, sequence.size() - 1);

		//printPiece(pieces);

		return pieces;
	}

	private void addPiece(ArrayList<Piece> pieces, int start, int end) {
		// TODO Auto-generated method stub
		Piece piece = new Piece();
		piece.setStart(start);
		piece.setEnd(end);
		piece.setSize(end - start + 1);
		pieces.add(pieces.size(), piece);
	}

	// public ArrayList getYears() {
	// return year_sequence;
	// }

	public void setYears(ArrayList<Integer> sequence) {

	}

	public ArrayList getSequence() {
		return sequence;
	}

	public void setSequence(ArrayList sequence) {
		this.sequence = sequence;
	}

	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(ArrayList<Piece> pieces) {
		this.pieces = pieces;
	}

	public ArrayList<Integer> getOppo_sequence() {
		return oppo_sequence;
	}

	public void setOppo_sequence(ArrayList<Integer> oppo_sequence) {
		this.oppo_sequence = oppo_sequence;
	}

	public ArrayList<HashSet> getSet_sequence() {
		return set_sequence;
	}

	public void setSet_sequence(ArrayList<HashSet> set_sequence) {
		this.set_sequence = set_sequence;
	}

	public ArrayList<HashSet> getOppo_set_sequence() {
		return oppo_set_sequence;
	}

	public void setOppo_set_sequence(ArrayList<HashSet> oppo_set_sequence) {
		this.oppo_set_sequence = oppo_set_sequence;
	}

	public ArrayList<String> getCata_sequence() {
		return cata_sequence;
	}

	public void setCata_sequence(ArrayList<String> cata_sequence) {
		this.cata_sequence = cata_sequence;
	}

	public int checkConsistency(Piece new_piece, int j, int i) {
		// TODO Auto-generated method stub
		// j is the first piece, i is the ending piece
		int new_position = i;
		for (int m = j; m <= i; m++) {
			Piece piece = pieces.get(m);
			//System.out.println("\t\t\t piece "+m+"\t"+piece.isIncrease()+"\t"+piece.size);
			if (new_piece.isIncrease() != piece.isIncrease() && piece.size > 1 && piece.isChange()) {
				System.out.println("\t\t\t\t split piece " + m);
				ArrayList<Piece> temp = new ArrayList<Piece>();

				temp = piece.splitPiece(set_sequence);

				pieces.remove(m);
				choppings.remove(m);
				gains.remove(m);

				for (int n = 0; n < temp.size(); n++) {
					Piece temp_piece = temp.get(n);
					HashSet years1 = set_sequence.get(temp_piece.getStart());
					HashSet years2 = set_sequence.get(temp_piece.getEnd());
					if (years1.equals(years2))
						temp_piece.setChange(false);
					else
						temp_piece.setChange(true);
					pieces.add(m + n, temp_piece);

					double gain = temp_piece.computeSetGain(set_sequence, oppo_set_sequence, deltat);
					temp_piece.setGain(gain);

					//System.out.println("\t"+piece.size+"\t"+piece.getGain()+"\t"+piece.isIncrease());

					// add piece i to chopping i
					ArrayList<Piece> chopping = new ArrayList<Piece>();
					chopping.add(chopping.size(), temp_piece);

					// add chopping i to choppings; add gain i to gains
					choppings.add(m + n, chopping);
					gains.add(m + n, gain);
				}
				new_position = m;
				return new_position;
			}
		}

		return new_position;
	}

	public ArrayList<Piece> simplePiece() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		addPiece(pieces, 0, sequence.size() - 1);
		return pieces;
	}

	public ArrayList<Piece> setSeriesPiece(ArrayList<LinkedHashMap<Integer, ReleaseLabel>> subSeries) {
		// TODO Auto-generated method stub
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int i = 0; i < subSeries.size(); i++) {
			LinkedHashMap<Integer, ReleaseLabel> map = subSeries.get(i);
			int start = 100000;
			int end = 0;
			for (Map.Entry<Integer, ReleaseLabel> entry : map.entrySet()) {
				int key = entry.getKey();
				start = Math.min(start, key);
				end = Math.max(end, key);
			}
			// System.out.println("series: [" + start + "\t" + end + "]");
			addPiece(pieces, start, end);
		}
		return pieces;
	}


}
