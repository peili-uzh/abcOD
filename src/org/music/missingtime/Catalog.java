package org.music.missingtime;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Catalog {

	public int id;

	public LinkedHashMap<Integer, String> strs;
	public LinkedHashMap<Integer, Integer> ints;
	public String prefix;
	public int value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LinkedHashMap<Integer, String> getStrs() {
		return strs;
	}

	public void setStrs(LinkedHashMap<Integer, String> strs) {
		this.strs = strs;
	}

	public LinkedHashMap<Integer, Integer> getInts() {
		return ints;
	}

	public void setInts(LinkedHashMap<Integer, Integer> ints) {
		this.ints = ints;
	}


	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Catalog() {
	}

	;

	public Catalog(String str) {
		strs = new LinkedHashMap<Integer, String>();
		ints = new LinkedHashMap<Integer, Integer>();

		boolean first = true;
		//System.out.println(str+"\t"+str.length());
		for (int i = 0; i < str.length(); i++) {
			String value = str.substring(i, i + 1);
			boolean digit = value.matches("[0-9]");
			//System.out.println("\t"+value+"\t"+digit);
			/*
			if(digit){
				if(first){
					if(Integer.valueOf(value)!=0){
						ints.put(i, Integer.valueOf(value));
						first = false;
					}
						
				}
				else 
					ints.put(i, Integer.valueOf(value));
			}*/
			if (digit)
				ints.put(i, Integer.valueOf(value));
			else
				strs.put(i, value);
		}

		prefix = strs.toString();
		//System.out.println("\t"+strs+"\t"+strs.toString());
		//System.out.println("\t"+ints);
	}

	public int oldcompareCatalog(Catalog c1, Catalog c2) {
		int order = 0;

		String prefix1 = c1.getPrefix();
		String prefix2 = c2.getPrefix();

		if (prefix1.equals(prefix2)) {
			/*
			 * if c1.ints and c2.ints are different
			 */
			LinkedHashMap<Integer, Integer> ints1 = c1.getInts();
			LinkedHashMap<Integer, Integer> ints2 = c2.getInts();
			if (ints1.size() > ints2.size())
				order = 1; // if size1 > size 2, order = 1
			else if (ints1.size() < ints2.size())
				order = -1; // size1 < size 2, order = -1
			else if (ints1.size() == ints2.size()) {
				boolean stop = false;
				Set<Integer> set = ints1.keySet();
				Iterator it = set.iterator();
				while (it.hasNext() && !stop) {
					int position = (Integer) it.next();
					int v1 = ints1.get(position);
					int v2 = ints2.get(position);
					int distance = v1 - v2;
					//System.out.println("\t\t"+v1+"\t"+v2+"\t"+distance);

					if (distance != 0) {
						stop = true;
						order = distance;
					}
				}
			}

		} else
			order = -1; // if prefix are different, order = -1
		/*
		if(order <0)
			System.out.println(c1.getInts()+"\t < \t"+c2.getInts());
		else if(order > 0)
			System.out.println(c1.getInts()+"\t > \t"+c2.getInts());
		else 
			System.out.println(c1.getInts()+"\t = \t"+c2.getInts());
		*/
		return order;
	}

	public long compareCatalog(Catalog c1, Catalog c2) {
		long order = 0;

		String prefix1 = c1.getPrefix();
		String prefix2 = c2.getPrefix();

		if (prefix1.equals(prefix2)) {
			/*
			 * if c1.ints and c2.ints are different
			 */
			LinkedHashMap<Integer, Integer> ints1 = c1.getInts();
			LinkedHashMap<Integer, Integer> ints2 = c2.getInts();

			//System.out.println(ints1+"\t"+ints2);
			long value1 = parseIntoInt(ints1);
			long value2 = parseIntoInt(ints2);

			order = value1 - value2;

		} else
			order = -1; // if prefix are different, order = -1

		return order;
	}


	public long parseIntoInt(LinkedHashMap<Integer, Integer> ints) {
		// TODO Auto-generated method stub

		long value = 0;
		int size = ints.size() - 1;
		int i = 0;
		for (Map.Entry<Integer, Integer> e : ints.entrySet()) {
			int number = e.getValue();
			value += number * Math.pow(10, (size - i));

			i++;
		}
		return value;
	}


}
