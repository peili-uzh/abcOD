package org.music.missingtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ParameterProcessor extends SyntheticData {

    public ParameterProcessor(int range,
                              LinkedHashMap<Integer, Integer> timelist, double percent, int deltat,
                              int theta) throws Exception {
        super(range, timelist, percent, deltat, theta);
    }

    public void learnBandWidth() throws Exception {
        System.out.println("learning delta t");
        int maxT = 0;
        int maxD = 0;
        int preSize = -1;
        int deltat = 0;//this.sequence.size();
        int range = 100;
        int theta = 15;

        System.out.println("delta t \t theta \t no. errors \t avg. distance \t num. of series \t avg series size \t min series size \t max series size");
        for (int i = deltat; i <= deltat; i++) {
            this.deltat = i;
            for (int j = 1; j <= theta; j++) {
                this.theta = j;
                detectErrors();
                //System.out.println(distanceSequence);
                System.out.println(i + "\t" + j + "\t" + errorSequence.size() + "\t" + getAverage(distanceSequence) + "\t" + this.numSeries + "\t" + this.avgSeriesSize + "\t" + this.minSeriesSize + "\t" + this.maxSeriesSize);
            }
        }
		
		/*while (t < 20){
			this.theta = t;
			this.deltat = 1;
			detectErrors();
			System.out.println(t+"\t"+errorSequence.size()+"\t"+getAverage(distanceSequence)+"\t");
			
			if(preSize >=0){
				int diff = (Math.abs(preSize - errorSequence.size()));
				if(diff > maxD){
					maxD = diff;
					maxT = t;
				}
			}
			
			preSize = errorSequence.size();
			range = errorSequence.size();
			t++;
		}
		System.out.println("proper delta t \t"+maxT);*/
    }

    private double getAverage(HashMap<Integer, Integer> distanceSequence) {
        double avg = 0;
        Iterator<Integer> it = distanceSequence.values().iterator();
        while (it.hasNext()) {
            avg += Math.abs(it.next());
        }
        avg = avg / distanceSequence.size();
        return avg;
    }

}
