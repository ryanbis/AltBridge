package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Provides static methods for creating {@code List} instances easily, and other
 * utility methods for working with lists.
 * 
 * Note: This was copied from the com.google.android.collect.Lists class
 * 
 */
public class Lists {

	/**
	 * Creates an empty {@code ArrayList} instance.
	 * 
	 * <p>
	 * <b>Note:</b> if you only need an <i>immutable</i> empty List, use
	 * {@link Collections#emptyList} instead.
	 * 
	 * @return a newly-created, initially-empty {@code ArrayList}
	 */
	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}
	
	/**
	 * Creates an empty arraylist with a limit. Use this for larger
	 * lists, normally an Arraylist starts off with an array of 10, then
	 * doubles each time it needs more indexes. When working with large lists, it's
	 * best to create the list with a set large size first, for better performance
	 * 
	 * @param size - the limit of the arraylist
	 * @return
	 */
	public static <E> ArrayList<E> newArrayList(int size) {
		return new ArrayList<E>(size);
	}

	/**
	 * Creates a resizable {@code ArrayList} instance containing the given
	 * elements.
	 * 
	 * <p>
	 * <b>Note:</b> due to a bug in javac 1.5.0_06, we cannot support the
	 * following:
	 * 
	 * <p>
	 * {@code List<Base> list = Lists.newArrayList(sub1, sub2);}
	 * 
	 * <p>
	 * where {@code sub1} and {@code sub2} are references to subtypes of
	 * {@code Base}, not of {@code Base} itself. To get around this, you must
	 * use:
	 * 
	 * <p>
	 * {@code List<Base> list = Lists.<Base>newArrayList(sub1, sub2);}
	 * 
	 * @param elements
	 *            the elements that the list should contain, in order
	 * @return a newly-created {@code ArrayList} containing those elements
	 */
	public static <E> ArrayList<E> newArrayList(E... elements) {
		int capacity = (elements.length * 110) / 100 + 5;
		ArrayList<E> list = new ArrayList<E>(capacity);
		Collections.addAll(list, elements);
		return list;
	}

	/**
	 * Alphabetizes an ArrayList of Strings.
	 * 
	 * @param list
	 *            - the list to alphabetize
	 * @return the alphabetized list
	 */
	public static ArrayList<String> alphabetize(ArrayList<String> list) {
		Collections.sort(list);
		return list;
	}

	/**
	 * Alphabetizes a DoubleList, assuming the first list contains Strings, and 
	 * the first list is the list to use to sort.
	 * 
	 * @param list
	 * @return
	 */
	public static DoubleList alphabetizeByFirst(DoubleList list) {
		int size = list.size();
		List<NameValuePair> pairs = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			pairs.add(new BasicNameValuePair(list.get(i)[0].toString(), list.get(i)[1].toString()));
		}		
		Collections.sort(pairs, new Comparator<NameValuePair>() {
			@Override
			public int compare(NameValuePair lhs, NameValuePair rhs) {				
				return lhs.getName().compareTo(rhs.getName());
			}
		});
		list.clearDoubleList();
		for (int i = 0; i < size; i++) {
			list.add(pairs.get(i).getName(), pairs.get(i).getValue());
		}
		return list;
	}
	
	/**
	 * Alphabetizes a DoubleList, assuming the second list contains Strings, and 
	 * the second list is the list to use to sort.
	 * 
	 * @param list
	 * @return
	 */
	public static DoubleList alphabetizeBySecond(DoubleList list) {
		int size = list.size();
		List<NameValuePair> pairs = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			pairs.add(new BasicNameValuePair(list.get(i)[0].toString(), list.get(i)[1].toString()));
		}		
		Collections.sort(pairs, new Comparator<NameValuePair>() {
			@Override
			public int compare(NameValuePair lhs, NameValuePair rhs) {				
				return lhs.getValue().compareTo(rhs.getValue());
			}
		});
		list.clearDoubleList();
		for (int i = 0; i < size; i++) {
			list.add(pairs.get(i).getName(), pairs.get(i).getValue());
		}
		return list;
	}

}
