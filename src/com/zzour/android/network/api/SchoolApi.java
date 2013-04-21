package com.zzour.android.network.api;

import java.util.ArrayList;

import android.util.Log;

import com.zzour.android.models.School;
import com.zzour.android.models.SchoolArea;

public class SchoolApi {
	
	private static final String TAG = "ZZOUR";
	
	private static ArrayList<School> mSchools = new ArrayList<School>();

	public static ArrayList<School> getSchoolList(){
		if (!mSchools.isEmpty() /* && not expired*/){
			return mSchools;
		}
		
		// TODO get from cache
		
		// TODO get from server
		// for demo, get from fake data
		String data = FakeData.getFakeSchool();
		return parseSchoolData(data);
	}
	
	private static ArrayList<School> parseSchoolData(String data){
		ArrayList<School> schools = new ArrayList<School>();
		String[] ss = data.split("\\|");
		for (int i=0; i < ss.length; i++){
			if (ss[i].length() == 0){
				continue;
			}
			String[] words = ss[i].split(",");
			if (words.length != 3){
				Log.e(TAG, "error in school data: " + ss[i]);
				return mSchools;
			}
			School school = new School(words[0]);
			String[] areas = words[1].split(";");
			String[] details = words[2].split(";");
			ArrayList<SchoolArea> l = new ArrayList<SchoolArea>();
			for (int j=0; j < areas.length; j++){
				if (areas[j].length() == 0){
					continue;
				}
				SchoolArea area = new SchoolArea(areas[j]);
				area.setDetails(details[j].split(":"));
				l.add(area);
			}
			school.setArea(l);
			schools.add(school);
		}
		mSchools = schools;
		return mSchools;
	}
}
