package br.ufpb.dicomflow.util;


public interface PagerIF {
	
	public static final int INITIAL_PAGE = 1;
	public static final int DEFAULT_PAGE_NUMBER = 1;
	public static final int DEFAULT_PAGE_QUANTITY = 25;
	public static final String NEXT_PAGE = "next";
	public static final String BACK_PAGE = "back";
	public static final String TOP_PAGE = "top";
	
	public void pageUpdate(String command);
	
	public int getMax() ;

    public int getFirst();
    
    public void setSize(int newSize);
    
    public boolean isNotFirstPage();
    
    public boolean isNotLastPage();
    
}
