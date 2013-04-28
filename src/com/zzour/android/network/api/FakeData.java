package com.zzour.android.network.api;

public class FakeData {

	public static String getFackShopList(int lastModified, int sinceId, int maxId, int count){
		String result;
		result = "{" + 
					"	\"dsk\": \"������\"," +
					"	\"bs\": [\"\", \"\", \"\"]," + 
					"	\"shops\": [" +
					"		{" +
					"			\"id\": 1," +
					"			\"name\": \"������\"," +
					"			\"desc\": \"���Ψһ������רӪ�꣬Ҳ����𱬵Ĳ�����֮һ������ν������ͺȼ����࣡\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_89/other/store_logo.jpg\"," +
					"			\"rate\": 5," +
					"			\"new\": false" +
					"		}," +
					"		{" +
					"			\"id\": 2," +
					"			\"name\": \"����ţ���\"," +
					"			\"desc\": \"�������Ʒζ���ڵ�Ȫ�ݷ�ζ��С�ԣ����и�ɫ�ײ��ޣ�\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_8/other/store_logo.jpg\"," +
					"			\"rate\": 3," +
					"			\"new\": true" +
					"		}," +
					"		{" + 
					"			\"id\": 3," +
					"			\"name\": \"ɽ��һ��\"," +
					"			\"desc\": \"������У������������7���Ʒ���ϵ꣬��������ɽ��ζ��\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_46/other/store_logo.jpg\"," +
					"			\"rate\": 2," +
					"			\"new\": false" +
					"		}," +
					"		{" + 
					"			\"id\": 4," +
					"			\"name\": \"��������ׯ��ѩ�����ԣ�\"," +
					"			\"desc\": \"����רӪ��ʽ��ʳ������ḻ��ζ����������ֵ��Ʒ����\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_69/other/store_logo.jpg\"," +
					"			\"rate\": 1," +
					"			\"new\": false" +
					"		}," +
					"		{" + 
					"			\"id\": 5," +
					"			\"name\": \"��������ׯ�����ֺ����ԣ�\"," +
					"			\"desc\": \"������˸�����ʳ�������ṩ����ײ��보�������ĺóԣ�\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_70/other/store_logo.jpg\"," +
					"			\"rate\": 4," +
					"			\"new\": true" +
					"		}," +
					"		{" + 
					"			\"id\": 6," +
					"			\"name\": \"�οͼ�С�ԣ�ѩ������棩\"," +
					"			\"desc\": \"�������οͼ�ʳ�����������οͼ�С�ԣ���Ҷ�����~\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_58/other/store_logo.jpg\"," +
					"			\"rate\": 5," +
					"			\"new\": false" +
					"		}" +
					"	]" +
					"}";
		return result;
	}
	
	public static String getFakeShopDetailById(int id){
		String result = "{" + 
							"\"banner\": \"http://www.zzour.com/data/files/store_89/other/store_logo.jpg\"," +
							"\"addr\": \"���ȴ�ѧ����\"," +
							"\"rcmds\": [" + 
								"{" +
									"\"id\": 123," +
									"\"name\": \"Ƥ��������\"," +
									"\"price\": 10.5," +
									"\"soldCount\": 46," +
									"\"image\": \"http://www.zzour.com/data/files/store_89/goods_89/small_201304111808091641.jpg\"" +
								"}," +
								"{" + 
									"\"id\": 124," +
									"\"name\": \"�ɱ�������\"," +
									"\"price\": 8.0," +
									"\"soldCount\": 25," +
									"\"image\": \"http://www.zzour.com/data/files/store_89/goods_118/small_201304111808382679.jpg\"" +
								"}" +
							"]," +
							"\"foods\": {" +
								"\"����\": [" +
									"{" + 
										"\"id\": 123," +
										"\"name\": \"����\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}," +
									"{" + 
										"\"id\": 124," +
										"\"name\": \"±��\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 26" +
									"}," +
									"{" + 
										"\"id\": 125," +
										"\"name\": \"Ѽ��\"," +
										"\"price\": 9.5," +
										"\"soldCount\": 98" +
									"}," +
									"{" + 
										"\"id\": 126," +
										"\"name\": \"ե��\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 37" +
									"}," +
									"{" + 
										"\"id\": 127," +
										"\"name\": \"�嵰\"," +
										"\"price\": 10.5," +
										"\"soldCount\": 16" +
									"}," +
									"{" + 
										"\"id\": 128," +
										"\"name\": \"����\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}" +
								"]," +
								"\"����\": [" +
									"{" + 
										"\"id\": 123," +
										"\"name\": \"����\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}," +
									"{" + 
										"\"id\": 124," +
										"\"name\": \"±��\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 26" +
									"}," +
									"{" + 
										"\"id\": 125," +
										"\"name\": \"Ѽ��\"," +
										"\"price\": 9.5," +
										"\"soldCount\": 9800" +
									"}," +
									"{" + 
										"\"id\": 126," +
										"\"name\": \"ե��\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 37" +
									"}," +
									"{" + 
										"\"id\": 127," +
										"\"name\": \"�嵰\"," +
										"\"price\": 10.5," +
										"\"soldCount\": 16" +
									"}," +
									"{" + 
										"\"id\": 128," +
										"\"name\": \"�������������\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}" +
								"]" +
							"}" +
						"}";
		return result;
	}
	
	public static String getFakeSchool(){
		String result = "���ȴ�ѧ,����У��;�麣У��,���Է2#��:���Է3#��:���Է4#��:���Է5#��;�Ͼ�Է2#��:�Ͼ�Է3#��:�Ͼ�Է4#��:�Ͼ�Է5#��|���Ŵ�ѧ,��У��;����У��,;��¥1��:��¥2��";
		return result;
	}
}
