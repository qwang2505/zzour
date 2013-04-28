package com.zzour.android.network.api;

public class FakeData {

	public static String getFackShopList(int lastModified, int sinceId, int maxId, int count){
		String result;
		result = "{" + 
					"	\"dsk\": \"佳粒粥\"," +
					"	\"bs\": [\"\", \"\", \"\"]," + 
					"	\"shops\": [" +
					"		{" +
					"			\"id\": 1," +
					"			\"name\": \"佳粒粥\"," +
					"			\"desc\": \"后街唯一的粥类专营店，也是最火爆的餐饮店之一！正所谓：喝粥就喝佳粒粥！\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_89/other/store_logo.jpg\"," +
					"			\"rate\": 5," +
					"			\"new\": false" +
					"		}," +
					"		{" +
					"			\"id\": 2," +
					"			\"name\": \"阿坤牛肉馆\"," +
					"			\"desc\": \"坤哥带你品味正宗的泉州风味名小吃，还有各色套餐噢！\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_8/other/store_logo.jpg\"," +
					"			\"rate\": 3," +
					"			\"new\": true" +
					"		}," +
					"		{" + 
					"			\"id\": 3," +
					"			\"name\": \"山西一绝\"," +
					"			\"desc\": \"自厦门校区招生以来，7年的品牌老店，绝对正宗山西味！\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_46/other/store_logo.jpg\"," +
					"			\"rate\": 2," +
					"			\"new\": false" +
					"		}," +
					"		{" + 
					"			\"id\": 4," +
					"			\"name\": \"碗碗香面庄（雪满天旁）\"," +
					"			\"desc\": \"本店专营各式面食，种类丰富，味道鲜美，很值得品尝！\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_69/other/store_logo.jpg\"," +
					"			\"rate\": 1," +
					"			\"new\": false" +
					"		}," +
					"		{" + 
					"			\"id\": 5," +
					"			\"name\": \"碗碗香面庄（苏乐汉堡旁）\"," +
					"			\"desc\": \"本店除了各类面食，还有提供多款套餐与炒饭，真心好吃！\"," +
					"			\"img\": \"http://www.zzour.com/data/files/store_70/other/store_logo.jpg\"," +
					"			\"rate\": 4," +
					"			\"new\": true" +
					"		}," +
					"		{" + 
					"			\"id\": 6," +
					"			\"name\": \"鑫客家小吃（雪满天对面）\"," +
					"			\"desc\": \"岛内有鑫客家食府，华大有鑫客家小吃！大家都懂得~\"," +
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
							"\"addr\": \"华侨大学后门\"," +
							"\"rcmds\": [" + 
								"{" +
									"\"id\": 123," +
									"\"name\": \"皮蛋瘦肉粥\"," +
									"\"price\": 10.5," +
									"\"soldCount\": 46," +
									"\"image\": \"http://www.zzour.com/data/files/store_89/goods_89/small_201304111808091641.jpg\"" +
								"}," +
								"{" + 
									"\"id\": 124," +
									"\"name\": \"干贝瘦肉粥\"," +
									"\"price\": 8.0," +
									"\"soldCount\": 25," +
									"\"image\": \"http://www.zzour.com/data/files/store_89/goods_118/small_201304111808382679.jpg\"" +
								"}" +
							"]," +
							"\"foods\": {" +
								"\"加料\": [" +
									"{" + 
										"\"id\": 123," +
										"\"name\": \"油条\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}," +
									"{" + 
										"\"id\": 124," +
										"\"name\": \"卤蛋\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 26" +
									"}," +
									"{" + 
										"\"id\": 125," +
										"\"name\": \"鸭蛋\"," +
										"\"price\": 9.5," +
										"\"soldCount\": 98" +
									"}," +
									"{" + 
										"\"id\": 126," +
										"\"name\": \"榨菜\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 37" +
									"}," +
									"{" + 
										"\"id\": 127," +
										"\"name\": \"煎蛋\"," +
										"\"price\": 10.5," +
										"\"soldCount\": 16" +
									"}," +
									"{" + 
										"\"id\": 128," +
										"\"name\": \"火腿\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}" +
								"]," +
								"\"粥类\": [" +
									"{" + 
										"\"id\": 123," +
										"\"name\": \"油条\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}," +
									"{" + 
										"\"id\": 124," +
										"\"name\": \"卤蛋\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 26" +
									"}," +
									"{" + 
										"\"id\": 125," +
										"\"name\": \"鸭蛋\"," +
										"\"price\": 9.5," +
										"\"soldCount\": 9800" +
									"}," +
									"{" + 
										"\"id\": 126," +
										"\"name\": \"榨菜\"," +
										"\"price\": 8.5," +
										"\"soldCount\": 37" +
									"}," +
									"{" + 
										"\"id\": 127," +
										"\"name\": \"煎蛋\"," +
										"\"price\": 10.5," +
										"\"soldCount\": 16" +
									"}," +
									"{" + 
										"\"id\": 128," +
										"\"name\": \"清蒸绝顶武昌鱼\"," +
										"\"price\": 8.0," +
										"\"soldCount\": 23" +
									"}" +
								"]" +
							"}" +
						"}";
		return result;
	}
	
	public static String getFakeSchool(){
		String result = "华侨大学,厦门校区;珠海校区,凤凰苑2#栋:凤凰苑3#栋:凤凰苑4#栋:凤凰苑5#栋;紫荆苑2#栋:紫荆苑3#栋:紫荆苑4#栋:紫荆苑5#栋|厦门大学,东校区;海边校区,;新楼1栋:新楼2栋";
		return result;
	}
}
