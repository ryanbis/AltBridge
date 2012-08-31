package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class TwinList<List1Type, List2Type> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 35577198350626168L;
	private ArrayList<List1Type> list1;
	private ArrayList<List2Type> list2;
	
	
	public TwinList() {
		list1 = new ArrayList<List1Type>();
		list2 = new ArrayList<List2Type>();
	}
	
	public TwinList(ArrayList<List1Type> list1, ArrayList<List2Type> list2) {
		this.list1 = list1;
		this.list2 = list2;
	}
	
	public TwinList(TwinList list) {
		list1 = list.getFirstList();
		list2 = list.getSecondList();
	}
	
	/**
	 *  Add data to the double list.
	 *  
	 * @param firstItem
	 * @param secondItem
	 */
	public void add(List1Type firstItem, List2Type secondItem) {
		list1.add(firstItem);
		list2.add(secondItem);
	}
	
	/**
	 *  Add data to the double list at the specified location. The item in
	 *  that location gets moved back (along with everything afterwards). If
	 *  the index is the size of the list, it gets added to the end.
	 *  
	 * @param index
	 * @param firstItem
	 * @param secondItem
	 */
	public void add(int index, List1Type firstItem, List2Type secondItem) {
		list1.add(index, firstItem);
		list2.add(index, secondItem);
	}
	
	/**
	 * 
	 *  Remove an item from the double list. (This removes an item from both lists
	 *  at the specified index position)
	 * @param index
	 */
	public void remove(int index) {
		list1.remove(index);
		list2.remove(index);
	}
	
	public List1Type getFirst(int index) {
		return list1.get(index);
	}
	
	public List2Type getSecond(int index) {
		return list2.get(index);
	}
	
	public ArrayList<List1Type> getFirstList() {
		return list1;
	}
	
	public ArrayList<List2Type> getSecondList() {
		return list2;
	}
	
	public TwinList<List1Type, List2Type> sublist(int start, int end) {
		TwinList<List1Type, List2Type> list = new TwinList<List1Type, List2Type>();		
		for (int i = start; i <= end; i++) {
			list.add(list1.get(i), list2.get(i));
		}
		return list;
	}
	
	public boolean firstListContains(List1Type item) {
		return list1.contains(item);
	}
	
	public boolean secondListContains(List2Type item) {
		return list2.contains(item);
	}
	
	/**
	 * 
	 * @return The size in indexes of this doublelist.
	 */
	public int size() {
		int one = list1.size();
		int two = list2.size();
		if (one != two) {
			return -1;
		}
		return one;
	}
	
	public int indexFirst(List1Type item) {
		return list1.indexOf(item);
	}
	
	public int indexSecond(List2Type item) {
		return list2.indexOf(item);
	}
	
	/**
	 * 
	 * @param item Object to check for in the doublelist
	 * @return the index of the object in the double list. If it's not found,
	 * -1 will be returned.
	 */
	public int indexAll(Object item) {
		int idx = list1.indexOf(item);
		if (idx == -1) {
			idx = list2.indexOf(item);
		}
		return idx;
	}
	
	/**
	 * 
	 *  Clears the twin list of all data.
	 *  
	 */
	public void clear() {
		list1.clear();
		list2.clear();
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object object) {
		if (object instanceof TwinList) {
			TwinList listcheck = (TwinList) object;
			if (listcheck.getFirstList().equals(list1) && listcheck.getSecondList().equals(list2)) {
				return true;
			}
		} 
		return false;
	}
	
	// Serializable implementation
		private void writeObject(ObjectOutputStream out) throws IOException {
			int size = size();
			out.writeInt(size);
			
			for (int i = 0; i < size; i++) {
				out.writeObject(list1.get(i));
				out.writeObject(list2.get(i));
			}
			
		}
		
		@SuppressWarnings("unchecked")
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			
			int size = in.readInt();
			list1 = new ArrayList<List1Type>();
			list2 = new ArrayList<List2Type>();
			for (int i = 0; i < size; i++) {
				list1.add((List1Type) in.readObject());
				list2.add((List2Type) in.readObject());
			}
			
		}
	
}
