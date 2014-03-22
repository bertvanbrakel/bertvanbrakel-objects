package com.bertvanbrakel.objects;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

public class Period {

	private final long time;
	private final TimeUnit units;
	
	public Period(long time, TimeUnit unit) {
		this.time = time;
		this.units = checkNotNull(unit, "expect units");
	}
	
	public TimeUnit getUnits(){
		return units;
	}
	
	public long getTime(){
		return time;
	}
	
	public static Period newMillisconds(long time){
		return new Period(time,TimeUnit.MILLISECONDS);
	}
	
	public static Period newSeconds(long time){
		return new Period(time,TimeUnit.SECONDS);
	}
	
	public static Period newMinutes(long time){
		return new Period(time,TimeUnit.MINUTES);
	}
	
	public static Period newHours(long time){
		return new Period(time,TimeUnit.HOURS);
	}
	
	public static Period newNanoSeconds(long time){
		return new Period(time,TimeUnit.NANOSECONDS);
	}
	
	public Period toUnits(TimeUnit toUnits){
		if( units == toUnits){
			return this;
		}
		return new Period(units.convert(time, toUnits),toUnits);
	}
	
	public long timeAs(TimeUnit toUnits){
		if( units == toUnits){
			return time;
		}
		return units.convert(time, toUnits);
	}
}
