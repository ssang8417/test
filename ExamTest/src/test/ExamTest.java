package test;

import java.util.HashMap;

public class ExamTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		System.out.println("test");
	 
	  String input = "abcdefgh";	 
	  forSoonyul(input);
	}

	private static void forSoonyul(String inputData) {
		// TODO Auto-generated method stub
		 
		
		
		String[] factory= new String[8];		  
				
		HashMap<Integer ,String> ha =new HashMap<Integer, String>();
		
		
		
		
		for(int i=0; i<inputData.length(); i++) ha.put(i,inputData.substring(i, i+1));
		for(int i=0; i<factory.length; i++) System.out.print(ha.get(i));  
		
		
		for(int i=0; i<factory.length; i++){
			for(int j=0; j<factory.length; j++ ){
				if(ha.get(i).equals(ha.get(j))){
					
				}
				
			}//for1
		}//for2
	}
}
	
	
//	 static int count=1;
//	 
//	public static void main(String[] args) { 
//	    int N = 8;   
//	    int n = 8; 
//	    int[] ptrArr = new int[n];     // alternative of i, j, k, ...         
//	    forAsMethod(N, n, ptrArr, 0);  // alternative of FOR statement 
//	} 
//
//	private static void forAsMethod(int N, int n, int[] ptrArr, int currPtr) { 
//	    LOOP :  
//	    for(int i = 0; i < N; i++) {
//	    	
//	        ptrArr[currPtr] = i;        // save in ptrArr 
//	        for(int j = 0; j < currPtr; j++) { 
//	            if(ptrArr[currPtr] == ptrArr[j]) 
//	                continue LOOP; 
//	        } 
//	        if(currPtr == n - 1) {      // all pointers are set 
//	           
//	            for(int p : ptrArr){
//	                System.out.print(p);
//	            } 
//	            System.out.println(":"+count); 
//	            count++;
//	        } 
//	        else 
//	            forAsMethod(N, n, ptrArr, currPtr + 1); 
//	    }             
//	} 
//}
