import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Scanner;



public class SkipListImpl<T extends Comparable<? super T>> implements
SkipList<T>  {
	private Node<T> header;  // dummy header node that points to the actual list
	private  int size = 0;
	private int height=3;
	private int maxHeight=3;
	SkipListImpl() {
		header = new Node<>(null,maxHeight);  header.next[0]= null; size = 0; 
		}
	
	  private  class Node<E> {  
			E data;
			Node<E>[] next;
			int[] Skips;
			@SuppressWarnings("unchecked")
			Node(E x,int l) { data = x;next=(Node<E>[])new Node[l];Skips=new int[l] ;}
		    }
	  
	
	public int build(List<T> arr)
	{
		// the build function can create a skiplist taking an array as  input it builds a perfect skip list
		Integer height=(int)( Math.log(arr.size())/Math.log(2))+1;
		this.height=height;
		ListIterator<T> itr=arr.listIterator();
		while(itr.hasNext())
		{
			addFront(itr.next());
		}
		int lvl=1;
		//System.out.println("Height "+height);
		while(height>=lvl) 
		{
			newLvl(lvl);
			lvl=lvl+1;
		}
		
		return height;
	}
	
	
	@SuppressWarnings("unchecked")
	private void dynamic_resize(int curmax)
	{
		// the aim of this function is to resize the array 
		//and increase the height of each node to the required height for rebuild
		Node<T> prev=this.header;
		Node<T> temp=prev.next[0];
		prev.next=(Node<T>[])new Node[this.height];
		prev.Skips=new int[this.height];
		prev.next[0]=temp;
		prev=prev.next[0];
		int i=1; 
		// this is the pointer for the location of the node in the list 1 for first element and so on
		while(prev!=null)// the outer loop traverses through the nodes
		{
		int j=this.height-1;
		while(j>=0)// the inner loop finds the height required for the node
		{
			// the formula is i%pow(2,j)==0  the value of j+1 is the no of elements
			//we want in our next array in a perfect skip list
			if(i%(1<<j)==0)
			{
				break;
			}
			j--;
		}
		temp=prev.next[0];
		prev.next=(Node<T>[])new Node[j+1];
		prev.Skips=new int[j+1];
		
		prev.next[0]=temp;
		i++;
		prev=prev.next[0];
		}	
		
	}
	public void rebuild()
	{
		Integer height=(int)( Math.log(this.size-1)/Math.log(2))+1;// the height of our skip list
		this.height=height;
		this.maxHeight=height;
		while(height>this.header.next.length)// call to dynamic resize if required (there is no node higher than the header )
		{
			int curmax=this.header.next.length;
			dynamic_resize(curmax);
		}
		
		int lvl=1;
		//System.out.println("Height "+height);
		while((height-1)>lvl) // we already have the level 0 as our base , we use the next level function to update the higher level nodes to make a perfect skip list
		{
			newLvl(lvl);
			lvl=lvl+1;
		}
		
		//return null;
		update_Skips();// this functionality is for the find index logic 
	}
	private void newLvl(int lvl)
	{
		// this method takes a skip list and level  and adds a new level upon it
		LstIterator<T> itrsl=(SkipListImpl<T>.LstIterator<T>) this.iterator();
		int i=0;
		//lvl=lvl-1;
		Node<T> n=this.header;
		Node<T> p=this.header;
		itrsl.lvl=lvl-1;
		while(itrsl.hasNext())// iterate the lower level
		{
			if(i%2==0)// for every even element in the lower level update the upper level 
			{
				//System.out.println(i+" "+lvl);
				p.next[lvl]=n;
				p=n;

			}
			i=i+1;
			n=n.next[lvl-1];
			itrsl.next();
		}
		//this if is required because when you have the last element as odd we dont have a hasnext for it and it gets dropped in the next level
		if(i%2==0)
		{
			p.next[lvl]=n;
		}
		
	}
	
	private void update_Skips()
	{// as it is  a perfect skip list we can update the skips of a node based on its previous skips this function is meant to be used only for a perfect skip list
		Node<T> n=this.header;
		//n=n.next[0];
		while(n.next[0]!=null)
		{
			for(int i=0;i<n.next.length;i++)
			{
				if(n.next[i]!=null &&  i!=0)
				{
					n.Skips[i]=2*n.Skips[i-1];
				}
				if(i==0)
				{
					n.Skips[i]=1;
				}
			}
			n=n.next[0];
		}
	}
	//incomplete implementation find index
	public T findIndex(int n)
	{
		//setSkips();
		Node<T> nn=this.header;
		for(int i=this.maxHeight-1;i>=0;i--)
		{
			if(header.Skips[i]>0)
			{
				if(header.Skips[i]<=n)
				{
					nn=nn.next[i];
				}
			}
		}
		//nn=nn.next[0];
		int i=this.maxHeight-1;
		int n_out=1;
		while(n_out<n)
		{
			System.out.println(i);
			System.out.println(nn+" "+nn.Skips[i]);
			if(nn.Skips[i]!=0)
			{
				if(n_out+nn.Skips[i]>n)
				{
					System.out.println("Test "+nn.Skips[i]+" Element "+nn.data);
					i=i-1;
				}
				else
				{
					n_out=n_out+nn.Skips[i];
					System.out.println("Out "+n_out+" "+nn.data+" "+i);
					nn=nn.next[i];
				}
			}
			else
			{
				i=i-1;
			}
		}
		if(n_out==n)
		{
			return nn.data;
		}
		else
		{
		return null;
		}
	}
	

	public void addFront(T x) {
		// adds a new element at the start of the list.. used to create the basic linked list used to build the skip list furhter
		Node<T> n = new Node<>(x,this.height);
		n.next[0] = header.next[0];
		//System.out.println(n.next);
		header.next[0]= n;
		if(header.next[0]==null)
		{
			n.Skips[0]=0;
		}
		else
		{
			n.Skips[0]=1;
		}
		size++;
	    }
	
	 public Iterator<T> iterator() { return new LstIterator<T>(this.header); } 
	 // returns a iterator for the skip list the default level of the iterator is 0, can be set
	 
	 private class LstIterator<E> implements Iterator<E> {
	    	Node<E> cur;  // cursor
	    	int lvl;
	    	LstIterator(Node<E> node) { cur = node; lvl=0; }// default level is set to 0 it will traverse the lowest level, can set it to any other value 
	    	public boolean hasNext() {return cur.next[lvl] != null; }
	    	public E next() { cur = cur.next[lvl];  return cur.data; }
	    	public void remove() {
	    	    }
	    	}
	        
	public void print(int lvl) {
		// prints any given level of the list
		LstIterator<T> itr=(SkipListImpl<T>.LstIterator<T>) this.iterator();
		itr.lvl=lvl;
		while(itr.hasNext())
		{
			T n= itr.next();
			System.out.print(n+" ");
	
		}
		
		
	    }
	public void printall(int h)
	{
		// prints the whole list till the specified level
		h=h-1;
		
		while (h>=0)
		{
			print(h);
			h=h-1;
			System.out.println("");
		}
	}
	
	private class Find_Node
	{// this is a special class used to hold list trace of the node searched .. required in add and delete
		private Node<T> p;
		private Node<T> prev[];
		private int choice=0;
		
		@SuppressWarnings("unchecked")
		Find_Node()
		{
			p=null;
			prev=new Node[maxHeight+1];	
		}
	}
	
	public Find_Node Find(T x)
	{
		Find_Node n=new Find_Node();
		Node<T> p=this.header;
//		System.out.println("To find "+x);
		if(p.next[0]==null)
		{
			n.p=null;
			n.prev[0]=this.header;
			return n;
		}
		else
		{
		
		for(int i=this.maxHeight-1;i>=0;i--)// starts from the top and checks for each node 
		{
			while(p.next[i]!=null && p.next[i].data.compareTo(x)<0)
			{
				p=p.next[i];
			}
			n.prev[i]=p;
			
		}
		
		if(p.next[0]!=null&&p.next[0].data.equals(x))
		{
			n.p=p.next[0];
			return(n);
		}
		else
		{
    		n.p=null;
			return(n);
		}
		
		}
	}
	
	public boolean remove(T x)
	{
		Find_Node p=Find(x);
		if(p.p==null)
		{
			System.out.println("Element not exists");
			return false;
		}
		else
		{
			for(int i=0;i<=this.maxHeight-1;i++)
			{
				if(p.prev[i].next[i]==p.p)
				{
					p.prev[i].next[i]=p.p.next[i];
				}
				else
				{
					break;
				}
			}
// incomplete code used to update skips on delete			
//			int j=0;
//			while(p.p.next[j]!=null)
//			{
//				j=j+1;
//			}
//			for(int i=1;i<=j;i++)
//			{
//				p.prev[i].Skips[i]=p.prev[i].Skips[i]+p.p.Skips[i]-1;
//				
//			}
//			
//			
//			for(int i=j;i<=this.maxHeight;i++)
//			{
//				if(p.prev[i]!=null  )
//				{
//				p.prev[i].Skips[i]--;
//				}
//				
//			}
			
			this.size--;
		}
		return true;
	}
	
	public void add(T x)
	{
		Find_Node p=Find(x);
		if(p.p!=null)
		{
			System.out.println("Element already exists");
		}
		else
		{
			int l =Choice(this.maxHeight);
//			System.out.println("Else height: "+l);
			Node<T> n=new Node<>(x,l+1);
			for(int i=l;i>=0;i--)
			{
//				System.out.println("\nBefore");
//				this.print(i);
				if(p.prev[i]!=null)
				{
				n.next[i]=p.prev[i].next[i];
				p.prev[i].next[i]=n;
				
				}
			}
			p.choice=l;
			p.p=n;
			//updateSkipV2(p);
						
	size++;

		
		}
	}
// update_header_skip and 	updateSkipV2 are incomplete functions to update skips
	private void update_header_skip(Node<T> n)
	{
		Node<T> nh= this.header;
		int val=1;
		header.Skips[0]=1;
		while(nh.next[0]!=n)
		{
			nh=nh.next[0];
			val++;
		}
		System.out.println("val of "+n.data+" "+val);
		int j=1;
		while(j<this.maxHeight)
		{
			if(this.header.next[j]==n)
			{
				this.header.Skips[j]=val-1;
			}
			j++;
		}
	}
	
	private void updateSkipV2(Find_Node p)
	{
		int lvl=p.choice;
		
		update_header_skip(p.p);
		
		int i=0;
		//System.out.println(p.p.data);
		if(p.p.next[0]==null)
		{
		//	System.out.println("Hi there!");
			p.prev[0].Skips[0]=1;
		}
		else
		{
			p.p.Skips[0]=1;
		}
		
		for(i=1;i<lvl;i++)
		{
			if(p.prev[i]!=null&&p.prev[i]!=this.header)
			{
			int prev_skips=p.prev[i].Skips[i];
			
			if(p.p.next[i]==null)
			{
				Node<T> Spl1=p.prev[i].next[i-1];
				int Spl1Skpis=0;
				while(Spl1!=p.p)
				{
					Spl1=Spl1.next[i-1];
					Spl1Skpis+=Spl1.Skips[i-1];
					
				}
				p.prev[i].Skips[i]=Spl1Skpis;
			}
			else
			{
			Node<T> nxt=p.p.next[i];
			Node<T> ptr=p.p;
			int prev_skipped=p.p.Skips[i-1];
			while(ptr.next[i-1].data!=null &&ptr.next[i-1]!=nxt)
			{
				ptr=ptr.next[i-1];
				prev_skipped=prev_skipped+ptr.Skips[i-1]+1;
			}
			p.prev[i].Skips[i]=prev_skipped-prev_skips;
			}
		}
		}
		while(i<this.maxHeight)
		{
			if(p.prev[i]!=null&&p.prev[i].next[i]!=null)
			{
				p.prev[i].Skips[i]++;
			}
			i++;
		}
		print_Skips();
		
	}
	
	private void print_Skips()
	{
		System.out.println("n nodes");
		Node<T> nh=this.header;
		while(nh.next[0]!=null)
		{System.out.println("for node "+nh.data);
			
		for(int k=0;k<this.maxHeight;k++)
		{
			System.out.println(k+" "+nh.Skips[k]);
		}
		System.out.println("n end");
		nh=nh.next[0];
		}
	}
	

	private int Choice(int maxLevel)
	{// chooses a random max level for a node
		int l=0;
		while(l<maxLevel)
		{
			Random rand = new Random();
			int b=rand.nextInt(2);
			if(b==0)
			{
				break;
			}
			else
			{
				l++;
			}
		}
		return l;
	}
	
	public boolean contains(T x)
	{
		Find_Node n= Find(x);
		if(n.p==null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public boolean isEmpty()
	{
		if(this.size==0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public T ceiling(T x)
	{
		
		Find_Node p=Find(x);
		if(p.p==null)
		{
			if(p.prev[0]!=null)
			{
			return p.prev[0].next[0].data;
			}
			else
			{
				return null;
			}
			
		}
		else
		{
			
			return p.p.data;
		}
		
	}
	
	public T floor(T x)
	{
		Find_Node p=Find(x);
		if(p.p==null)
		{
			if(p.prev[0]!=null)
			{
			return p.prev[0].data;
			}
			else
			{
				return null;
			}
		
			
		}
		else
		{
			
			return p.p.data;
		}
		
	}
	public int size()
	{
		return this.size;
	}
	public T first()
	{
		Node<T> n=this.header;
		if(n.next[0]==null)
		{
			return null;
		}
		else
		{
			return n.next[0].data;
		}
	}
	
	public T last()
	{
		Node<T> n=this.header;
		if(n.next[0]==null)
		{
			return null;
		}
		else
		{
			int i=this.height-1;
			
			while(n.next[0]!=null)
			{
				//n=n.next[i];// traveses to the end of each node
				while(n.next[i]!=null)
				{// goes to the end of the node
					n=n.next[i];
				}
				i--;
				
			}
			return n.data;
		}
	}
	
	public static void main(String[] args) {
//		SkipListImpl<Long> sl = new SkipListImpl<Long>();
//		sl.add((long)10); 
//		sl.add((long)11); 
//		sl.add((long)12); 
//		sl.add((long)13);
//		sl.add((long)15);
//		sl.add((long)14);
//	//	sl.printall(7);
	//	sl.findIndex(2);
		//sl.printall(2);
		//sl.remove((long) 11);
	
//    	sl.rebuild();
//    	sl.printall(sl.height);
//    	skipList.printall(5);
    	
//	System.out.println("At index "+sl.findIndex(4));
		
		Scanner sc = null;

		if (args.length > 0) {
			File file = new File(args[0]);
			try {
				sc = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			sc = new Scanner(System.in);
		}
//		String f="D:\\windows\\desktop\\implementation\\proj1-inputs (1)\\proj1-in1\\input_100000.txt";
//		File file= new File(f);
//		try {
//			sc=new Scanner(file);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String operation = "";
		long operand = 0;
		int modValue = 997;
		long result = 0;
		Long returnValue = null;
		SkipListImpl<Long> skipList = new SkipListImpl<Long>();
		// Initialize the timer
		long startTime = System.currentTimeMillis();

		while (!((operation = sc.next()).equals("End"))) {
			//System.out.println("Hi");
			
			switch (operation) {
			case "Add": {
				operand = sc.nextLong();
				skipList.add(operand);
				result = (result + 1) % modValue;
				break;
			}
//			case "Ceiling": {
//				operand = sc.nextLong();
//				System.out.println("Operand "+operand);
//				System.out.println("Hi");
//				returnValue = skipList.ceiling(operand);
//				if (returnValue != null) {
//					result = (result + returnValue) % modValue;
//				}
//				break;
//			}
//			case "FindIndex": {
//				operand = sc.nextLong();
//				returnValue = skipList.ceiling(operand);
//				if (returnValue != null) {
//					result = (result + returnValue) % modValue;
//				}
//				break;
//			}
//			case "First": {
//				returnValue = skipList.first();
//				if (returnValue != null) {
//					result = (result + returnValue) % modValue;
//				}
//				break;
//			}
//			case "Last": {
//				returnValue = skipList.last();
//				if (returnValue != null) {
//					result = (result + returnValue) % modValue;
//				}
//				break;
//			}
//			case "Floor": {
//				operand = sc.nextLong();
//				returnValue = skipList.floor(operand);
//				if (returnValue != null) {
//					result = (result + returnValue) % modValue;
//				}
//				break;
//			}
			case "Remove": {
				operand = sc.nextLong();
				if (skipList.remove(operand)) {
					result = (result + 1) % modValue;
				}
				break;
			}
			
			case "Contains": {
			operand = sc.nextLong();
			//System.out.println("Operand "+operand);
			//System.out.println("Hi");
			//returnValue = skipList.contains(operand);
			if (skipList.contains(operand)) {
				result = (result + 1) % modValue;
			}
			break;
		}

			}
		}

		// End Time
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		
		skipList.printall(skipList.height);
		skipList.rebuild();
    	skipList.printall(skipList.height);
	

		System.out.println(result + " " + elapsedTime);

	

	


	}

	
}
