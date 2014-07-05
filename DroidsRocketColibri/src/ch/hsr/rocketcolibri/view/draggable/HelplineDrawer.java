package ch.hsr.rocketcolibri.view.draggable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import ch.hsr.rocketcolibri.view.AbsoluteLayout;

public class HelplineDrawer{
	private Context tContext;
    private final int HELPLINE_OFFSET;
    private final int HELPDISTANCELINE_WIDTH_AND_HEIGHT;
    private View topHorizontalLine;
    private View leftVerticalLine;
    private View bottomHorizontalLine;
    private View rightVerticalLine;
    private View horizontalDistanceLine;
    private View verticalDistanceLine;
    private int[] tXPositions;
    private int[] tYPositions;
    private int[] tLeftDistances;
    private int[] tTopDistances;
    private int[] tRightDistances;
    private int[] tBottomDistances;
    private boolean horizontalDistanceTriggert;
    private boolean verticalDistanceTriggert;
    private int tTargetTouchPosition = 0;
    private int tClosestIndex = 0;
    private AbsoluteLayout.LayoutParams tParentLP;
    private AbsoluteLayout.LayoutParams tMovableViewLP;
    
	public HelplineDrawer(Context context, ViewGroup parent, AbsoluteLayout.LayoutParams parentLP, AbsoluteLayout.LayoutParams movableViewLP, int distanceWidth, int lineWidth, int offset, int lineColor, int distanceColor) {
		tContext = context;
		tParentLP = parentLP;
		tMovableViewLP = movableViewLP;
		HELPLINE_OFFSET = offset;
		HELPDISTANCELINE_WIDTH_AND_HEIGHT = distanceWidth;
		tXPositions = new int[parent.getChildCount()*2];
        tYPositions = new int[tXPositions.length];
        List<Integer> leftDistances = new ArrayList<Integer>();
        List<Integer> rightDistances = new ArrayList<Integer>();
        List<Integer> topDistances = new ArrayList<Integer>();
        List<Integer> bottomDistances = new ArrayList<Integer>();
        Integer distanceTmp = null;
        AbsoluteLayout.LayoutParams childViewLp = null;
        for(int i = 0, j = 0; j < parent.getChildCount();++j,i+=2){
        	childViewLp = (AbsoluteLayout.LayoutParams) parent.getChildAt(j).getLayoutParams();
        	tXPositions[i] = childViewLp.x;
        	if(tXPositions[i]>tParentLP.x+offset){
        		distanceTmp = Integer.valueOf(tParentLP.width-tXPositions[i]);
        		if(tParentLP.width/2>distanceTmp.intValue()){
        			leftDistances.add(distanceTmp);
        		}else{rightDistances.add(distanceTmp);}
        	}
        	tXPositions[i+1] = childViewLp.x+childViewLp.width;
        	if(tXPositions[i+1]<tParentLP.width-offset){
         		distanceTmp = Integer.valueOf(tParentLP.width-tXPositions[i+1]);
         		if(tParentLP.width/2>distanceTmp.intValue()){
         			leftDistances.add(distanceTmp);
         		}else{rightDistances.add(distanceTmp);}
        	}
        	tYPositions[i] = childViewLp.y;
        	if(tYPositions[i]>tParentLP.y+offset){
         		distanceTmp = Integer.valueOf(tParentLP.height-tYPositions[i]);
         		if(tParentLP.height/2>distanceTmp.intValue()){
         			topDistances.add(distanceTmp);
         		}else{bottomDistances.add(distanceTmp);}

        	}
        	tYPositions[i+1] = childViewLp.y+childViewLp.height;
        	if(tYPositions[i+1]<tParentLP.height-offset)
         		distanceTmp = Integer.valueOf(tParentLP.height-tYPositions[i+1]);
         		if(tParentLP.height/2>distanceTmp.intValue()){
         			topDistances.add(distanceTmp);
         		}else{bottomDistances.add(distanceTmp);}
        }
        tLeftDistances = listToArray(leftDistances);
        tRightDistances = listToArray(rightDistances);
        tTopDistances = listToArray(topDistances);
        tBottomDistances = listToArray(bottomDistances);
        Arrays.sort(tLeftDistances);
        Arrays.sort(tTopDistances);
        Arrays.sort(tRightDistances);
        Arrays.sort(tBottomDistances);
        Arrays.sort(tXPositions);
        Arrays.sort(tYPositions);
        topHorizontalLine = createLine(tParentLP.width, lineWidth, lineColor);
        bottomHorizontalLine = createLine(tParentLP.width, lineWidth, lineColor);
        leftVerticalLine = createLine(lineWidth, tParentLP.height, lineColor);
        rightVerticalLine = createLine(lineWidth, tParentLP.height, lineColor);
        
        horizontalDistanceLine = createDistanceLine(false, distanceWidth, distanceWidth, lineWidth, distanceColor);
        verticalDistanceLine = createDistanceLine(true, distanceWidth, distanceWidth, lineWidth, distanceColor);
	}
	
	public void drawAndFillStickyPosition(int[] xy){
    	//left
    	if(tLeftDistances.length>0 && Math.abs(tLeftDistances[tClosestIndex=findClosest(tLeftDistances, tTargetTouchPosition=xy[0])]-tTargetTouchPosition)<=HELPLINE_OFFSET){
    		horizontalDistanceTriggert = true;
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)horizontalDistanceLine.getLayoutParams());
    		lp.x = 0;lp.height=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.y=(tMovableViewLP.y+(tMovableViewLP.height/2-lp.height/2));lp.width=tLeftDistances[tClosestIndex];
    		horizontalDistanceLine.setVisibility(View.VISIBLE);
			xy[0] = tLeftDistances[tClosestIndex];
			leftVerticalLine.setVisibility(View.INVISIBLE);
    	}else{
    		horizontalDistanceTriggert = false;
    		horizontalDistanceLine.setVisibility(View.INVISIBLE);
			if(Math.abs(tXPositions[tClosestIndex=findClosest(tXPositions, tTargetTouchPosition=xy[0])]-tTargetTouchPosition)<=HELPLINE_OFFSET){
				((AbsoluteLayout.LayoutParams)leftVerticalLine.getLayoutParams()).x = tXPositions[tClosestIndex];
				leftVerticalLine.setVisibility(View.VISIBLE);
				xy[0] = tXPositions[tClosestIndex];
	    	}else leftVerticalLine.setVisibility(View.INVISIBLE);
    	}
    	//top
    	if(tTopDistances.length>0 && Math.abs(tTopDistances[tClosestIndex=findClosest(tTopDistances, tTargetTouchPosition=xy[1])]-tTargetTouchPosition)<=HELPLINE_OFFSET){
    		verticalDistanceTriggert = true;
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)verticalDistanceLine.getLayoutParams());
    		lp.width=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.x=tMovableViewLP.x+(tMovableViewLP.width/2-lp.width/2);lp.y=0;lp.height=tTopDistances[tClosestIndex];
    		verticalDistanceLine.setVisibility(View.VISIBLE);
    		xy[1] = tTopDistances[tClosestIndex];
    		topHorizontalLine.setVisibility(View.INVISIBLE);
    	}else{
    		verticalDistanceTriggert = false;
    		verticalDistanceLine.setVisibility(View.INVISIBLE);
    		if(Math.abs(tYPositions[tClosestIndex=findClosest(tYPositions, tTargetTouchPosition=xy[1])]-tTargetTouchPosition)<=HELPLINE_OFFSET){
    			((AbsoluteLayout.LayoutParams)topHorizontalLine.getLayoutParams()).y = tYPositions[tClosestIndex];
    			topHorizontalLine.setVisibility(View.VISIBLE);
    			xy[1] = tYPositions[tClosestIndex];
        	}else topHorizontalLine.setVisibility(View.INVISIBLE);
    	}
    	//right
    	if(!horizontalDistanceTriggert && tRightDistances.length>0 && Math.abs(tRightDistances[tClosestIndex=findClosest(tRightDistances, tTargetTouchPosition=xy[0]+tMovableViewLP.width)]-tTargetTouchPosition)<=HELPLINE_OFFSET){
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)horizontalDistanceLine.getLayoutParams());
    		lp.height=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.x = tRightDistances[tClosestIndex];lp.y=(tMovableViewLP.y+(tMovableViewLP.height/2-lp.height/2));lp.width=tParentLP.width-tRightDistances[tClosestIndex];
    		horizontalDistanceLine.setVisibility(View.VISIBLE);
    		xy[0] = tRightDistances[tClosestIndex]-tMovableViewLP.width;
    		rightVerticalLine.setVisibility(View.INVISIBLE);
    	}else{
			if(Math.abs(tXPositions[tClosestIndex=findClosest(tXPositions, tTargetTouchPosition=xy[0]+tMovableViewLP.width)]-tTargetTouchPosition)<=HELPLINE_OFFSET){
				((AbsoluteLayout.LayoutParams)rightVerticalLine.getLayoutParams()).x = tXPositions[tClosestIndex];
				rightVerticalLine.setVisibility(View.VISIBLE);
				xy[0] = tXPositions[tClosestIndex]-tMovableViewLP.width;
			}else rightVerticalLine.setVisibility(View.INVISIBLE);
    	}
    	//bottom
    	if(!verticalDistanceTriggert && tBottomDistances.length>0 && Math.abs(tBottomDistances[tClosestIndex=findClosest(tBottomDistances, tTargetTouchPosition=xy[1]+tMovableViewLP.height)]-tTargetTouchPosition)<=HELPLINE_OFFSET){
    		AbsoluteLayout.LayoutParams lp = ((AbsoluteLayout.LayoutParams)verticalDistanceLine.getLayoutParams());
    		lp.width=HELPDISTANCELINE_WIDTH_AND_HEIGHT;lp.x=(tMovableViewLP.x+(tMovableViewLP.width/2-lp.width/2));lp.height=tParentLP.height-tBottomDistances[tClosestIndex];lp.y=tBottomDistances[tClosestIndex];
    		verticalDistanceLine.setVisibility(View.VISIBLE);
    		xy[1] = tBottomDistances[tClosestIndex]-tMovableViewLP.height;
    		bottomHorizontalLine.setVisibility(View.INVISIBLE);
    	}else{
			if(Math.abs(tYPositions[tClosestIndex=findClosest(tYPositions, tTargetTouchPosition=xy[1]+tMovableViewLP.height)]-tTargetTouchPosition)<=HELPLINE_OFFSET){
				((AbsoluteLayout.LayoutParams)bottomHorizontalLine.getLayoutParams()).y = tYPositions[tClosestIndex];
				bottomHorizontalLine.setVisibility(View.VISIBLE);
				xy[1] = tYPositions[tClosestIndex]-tMovableViewLP.height;
			}else bottomHorizontalLine.setVisibility(View.INVISIBLE);
    	}
	}
	
    private View createDistanceLine(boolean vertical, int width, int height, int lineWidth, int color){
    	FrameLayout fl = new FrameLayout(tContext);
    	fl.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, 0, 0));
    	fl.setVisibility(View.INVISIBLE);
    	if(vertical){
	    	fl.addView(createGravityLine(-1, lineWidth, Gravity.TOP, color));
	    	fl.addView(createGravityLine(-1, lineWidth, Gravity.BOTTOM, color));
	    	fl.addView(createGravityLine(lineWidth, -1, Gravity.CENTER_HORIZONTAL, color));
    	}else{
	    	fl.addView(createGravityLine(lineWidth, -1, Gravity.LEFT, color));
	    	fl.addView(createGravityLine(lineWidth, -1, Gravity.RIGHT, color));
	    	fl.addView(createGravityLine(-1, lineWidth, Gravity.CENTER_VERTICAL, color));
    	}
    	return fl;
    }
    
    private View createGravityLine(int width, int height, int gravity, int color){
    	View line = new View(tContext);
        line.setBackgroundColor(color);
        line.setLayoutParams(new FrameLayout.LayoutParams(width<1?FrameLayout.LayoutParams.MATCH_PARENT:width,
        		height<1?FrameLayout.LayoutParams.MATCH_PARENT:height, gravity));
        return line;
    }

    private View createLine(int width, int height, int color){
    	View line = new View(tContext);
        line.setBackgroundColor(color);
        line.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, 0, 0));
        line.setVisibility(View.INVISIBLE);
        return line;
    }
	
	public void addLinesTo(ViewGroup parent) {
        parent.addView(topHorizontalLine);
        parent.addView(leftVerticalLine);
        parent.addView(rightVerticalLine);
        parent.addView(bottomHorizontalLine);
        parent.addView(horizontalDistanceLine);
        parent.addView(verticalDistanceLine);			
	}

	public void removeFrom(ViewGroup parent){
		parent.removeView(bottomHorizontalLine);
		parent.removeView(rightVerticalLine);
		parent.removeView(topHorizontalLine);
		parent.removeView(leftVerticalLine);
		parent.removeView(verticalDistanceLine);
		parent.removeView(horizontalDistanceLine);
		tContext = null;
		tParentLP = null;
		tMovableViewLP = null;
		
	}
	
	private int findClosest(int[] a, int key) {
		if (a.length == 0)
			return 0;
		int minIndex = 0;
		int maxIndex = a.length - 1;
		int curIn = (maxIndex + minIndex) / 2;
		while (true) {
			if (a[curIn] == key) {
				return curIn;
			} else if (a[curIn]<key) {
				minIndex = curIn + 1; // its in the upper
				if (minIndex > maxIndex){
					if(minIndex==a.length)return --minIndex;
					if((Math.abs(a[minIndex]-key)) < Math.abs(a[maxIndex]-key)){
						return minIndex;
					}else{
						return curIn + 1;
					}
				}else{
					curIn = minIndex;
				}
			} else {
				maxIndex = curIn - 1; // its in the lower
				if (minIndex > maxIndex){
					if(maxIndex<0)return 0;
					if(Math.abs(a[maxIndex]-key)<(Math.abs(a[minIndex]-key))){
						return maxIndex;
					}else
						return curIn;
				}else{
					curIn = maxIndex;
				}
			}
		}
	}

	private int[] listToArray(List<Integer> list){
    	int[] a = new int[list.size()];
    	for(int i = 0; i < list.size();++i){
    		a[i] = list.get(i).intValue();
    	}
    	return a;
    }
}