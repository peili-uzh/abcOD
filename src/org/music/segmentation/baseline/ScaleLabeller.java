package org.music.segmentation.baseline;


/**
 * It takes a list of extremal values in the input sequence, and label each of them with scales.
 * The scale labelling of an extremum x is the maximaum of the scales of the maximal *-pair it belongs to.
 *
 * @author peili
 */

public class ScaleLabeller {

    public ScaleLabeller() {
    }

    /**
     * Label the extremal values in lienar time.
     *
     * @param data SequenceList of extremal values
     * @return
     */
    public void scaleLabelling(SequenceList data) {

        //printSequenceList(data);

        SequenceList extrema = new SequenceList();

        for (int i = 0; i < data.size(); i++) {

            ExtremumRecord e = (ExtremumRecord) data.getElement(i);
            //System.out.println(i+"\t"+e.getIndex()+"\t"+e.getValue()+"\t"+e.getSense());

            int extr_size = extrema.size();
            while (extr_size > 2 && isExtrema(extrema, e)) {
                int scale = getScale(extrema);
                labelExtrem(1, scale, extrema);
                labelExtrem(0, scale, extrema);
                extr_size = extrema.size();
            }

            if (extrema.size() == 2 && isExtrema(extrema, e))
                labelExtrem(1, getScale(extrema), extrema);

            extrema.pushElement(e);
            //System.out.println("\t Add to stack new record: \t"+e.getIndex()+"\t"+e.getValue());
        }

        int size = extrema.size();
        while (size > 2) {
            //System.out.println(getScale(extrema));
            labelExtrem(0, getScale(extrema), extrema);
            size = extrema.size();
        }

        int scale = getScale(extrema);
        labelExtrem(1, scale, extrema);
        labelExtrem(0, scale, extrema);

        // System.out.println("end of labelling");

        //return scales;

    }

    /**
     * Print out each element of a SequenceList in order.
     *
     * @param scales
     */
    private void printSequenceList(SequenceList scales) {

        System.out.println();
        System.out.println("id \t index \t value \t sense \t scale");

        for (int i = 0; i < scales.size(); i++) {
            ExtremumRecord e = (ExtremumRecord) scales.getElement(i);
            System.out.println(i + "\t" + e.getIndex() + "\t" + e.getValue() + "\t" + e.getSense() + "\t" + e.getScale());
        }

    }

    /**
     * Label an ExtremumRecord by the specific scale value.
     *
     * @param i       index of an ExtremumRecord in a SequenceList
     * @param scale   value to be labelled
     * @param extrema a SequenceList
     */
    private void labelExtrem(int i, int scale, SequenceList extrema) {
        ExtremumRecord record = (ExtremumRecord) extrema.getElement(i); // get the second element in scales
        record.setScale(scale);
        //System.out.println("\t\t label record \t"+record.getValue()+"\t"+scale);
        extrema.remove(i);
    }
	
	/*
	private void labelExtrem(int i, SequenceList extrema, SequenceList scales) {
		// TODO Auto-generated method stub
		System.out.println("\t\t Label record \t"+i);
		ExtremumRecord record = (ExtremumRecord) extrema.getElement(i); // get the second element in scales
		int scale = getScale(extrema);
		record.setScale(scale);
		scales.pushElement(record);
		System.out.println("\t\t\t "+record.getValue()+"\t"+scale);
		
		extrema.remove(i);
	}*/

    /**
     * Compute the scale value of the top two elements in a SequenceList.
     *
     * @param extrema
     * @return scale value as an Integer.
     */
    private int getScale(SequenceList extrema) {
        int scale = 0;
        if (extrema.size() > 1) {
            ExtremumRecord second = (ExtremumRecord) extrema.getElement(1); // get the second element in scales
            ExtremumRecord first = (ExtremumRecord) extrema.getElement(0);
            scale = Math.abs(first.getValue() - second.getValue());
        }
        //System.out.println("scale \t"+scale);
        return scale;
    }

    /**
     * Check if element e is an global extremal element visited so far.
     *
     * @param extrema
     * @param e
     * @return
     */
    private boolean isExtrema(SequenceList extrema, ExtremumRecord e) {
        boolean extrem = false;

        //System.out.println("\t Check for extrem");

        if (extrema.size() > 1) {
            ExtremumRecord second = (ExtremumRecord) extrema.getElement(1); // get the second element in scales
            int distance = e.getValue() - second.getValue();
            //System.out.println("\t\t"+second.getSense()+"\t"+second.getValue());
            //System.out.println("\t\t"+e.getSense()+"\t"+e.getValue());

            extrem = (e.getSense().equals("min") && distance <= 0) || (e.getSense().equals("max") && distance >= 0);
            //System.out.println("\t\t"+extrem);

        }

        return extrem;
    }

}
