import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;

/** Graph -- makes a graph with vertices and edges for Dijkstra to be performed.

@author Izan Ahmed
@author Mark Allen Weiss
@author Shervin Hajiamini

Grinnell College
ahmediza@grinnell.edu

An object of this creates a graph.
*/

// Used to signal violations of preconditions for
// various shortest path algorithms.

class GraphException extends RuntimeException
{
    public GraphException( String name )
    {
        super( name );
    }
}

// Represents an edge in the graph.
class Edge
{
    public Vertex     dest;   // Second vertex in Edge
    public double     cost;   // Edge cost
    
    public Edge( Vertex d, double c )
    {
        dest = d;
        cost = c;
    }
}

// Represents an entry in the priority queue for Dijkstra's algorithm.

class Path implements Comparable<Path>
{
    public Vertex     dest;   // w
    public double     cost;   // d(w)
    
    public Path( Vertex d, double c )
    {
        dest = d;
        cost = c;
    }
    
    public int compareTo( Path rhs )
    {
        double otherCost = rhs.cost;
        
        return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
    }
}

// Represents a vertex in the graph.

class Vertex
{
    public String     name;   // Vertex name
    public List<Edge> adj;    // Adjacent vertices
    public double     dist;   // Cost
    public Vertex     prev;   // Previous vertex on shortest path
    public int        scratch;// Extra variable used in algorithm

    public Vertex( String nm )
      { name = nm; adj = new LinkedList<Edge>( ); reset( ); }

    public void reset( )
      { dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }    
      
    public PairingHeap.Position<Path> pos;  // Used for dijkstra2 (Chapter 23)
}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )   --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void dijkstra( String s )    --> Single-source weighted
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by dijkstra's
// algorithm.  Exceptions are thrown if errors are detected.

public class Graph
{
    public static final double INFINITY = Double.MAX_VALUE;
    private Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );
    
    /**
     * Add a new edge to the graph.
     */
    
    public void addEdge( String sourceName, String destName, double cost )
    {
        Vertex v = getVertex( sourceName );
        Vertex w = getVertex( destName );
        v.adj.add( new Edge( w, cost ) );
    }

    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run. Writes to a file.
     */
    
    public void printPath( String destName, PrintWriter wrt)
    {
    	Vertex w = vertexMap.get( destName );
    	if( w == null )
    		throw new NoSuchElementException( "Destination vertex not found" );
    	else if( w.dist == INFINITY )
    		wrt.println( destName + " is unreachable" );
    	else
    	{
    		wrt.print( "(Cost is: " + w.dist + ") " );
    		printPath( w , wrt);
    		wrt.println( );
    	}
    }

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    
    private Vertex getVertex( String vertexName )
    {
    	Vertex v = vertexMap.get( vertexName );
    	if( v == null )
    	{
    		v = new Vertex( vertexName );
    		vertexMap.put( vertexName, v );
    	}
    	return v;
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist. Writes to a file.
     */

    private void printPath( Vertex dest, PrintWriter wrt)
    {
    	if( dest.prev != null )
    	{
    		printPath( dest.prev, wrt);
    		wrt.print( " to " );
    	}
    	wrt.print( dest.name );
    }

    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
     */
    private void clearAll( )
    {
    	for( Vertex v : vertexMap.values( ) )
    		v.reset( );
    }

    /**
     * Single-source weighted shortest-path algorithm.
     */
    public void dijkstra( String startName )
    {
    	PriorityQueue<Path> pq = new PriorityQueue<Path>( );

    	Vertex start = vertexMap.get( startName );
    	if( start == null )
    		throw new NoSuchElementException( "Start vertex not found" );

    	clearAll( );
    	pq.add( new Path( start, 0 ) ); start.dist = 0;

    	int nodesSeen = 0;
    	while( !pq.isEmpty( ) && nodesSeen < vertexMap.size( ) )
    	{
    		Path vrec = pq.remove( );
    		Vertex v = vrec.dest;
    		if( v.scratch != 0 )  // already processed v
    			continue;
                
            v.scratch = 1;
            nodesSeen++;

            for( Edge e : v.adj )
            {
                Vertex w = e.dest;
                double cvw = e.cost;
                
                if( cvw < 0 )
                    throw new GraphException( "Graph has negative edges" );
                    
                if( w.dist > v.dist + cvw )
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    pq.add( new Path( w, w.dist ) );
                }
            }
        }
    }

    /**
     * performDijkstra: performs Dijkstra, takes in a Graph, and a PrintWriter, to print it
     * 				   directly to a file.
     */ 

    public void performDijkstra(Graph g, PrintWriter wrt)
    {
    	System.out.println( "Using Dijkstra's Algorithm..." );	
    	for(int i = 1; i <= Integer.parseInt("1000"); i++) {
    		g.dijkstra( "0" );
    		g.printPath(Integer.toString(i), wrt);
    	}
    }

    /**
     * convert: This function takes in an int and sets the cost, using log base 2 for the edges 
     * 			connecting 2 vertices twice of each other.
     */

    public double convert(int x)
    {
    	return (x * (1 +(Math.log(x) / Math.log(2))));
    }

    /**
     * exponentFileMaker: This function takes in the input filename and adds the vertices
     * 					  with the corresponding costs to the input file.
     */
    
    public void exponentFileMaker(String inputFileName) {
    	try {
    		File file = new File(inputFileName);		
    		if (!file.exists()) {
    			file.createNewFile();
    		}			
    		PrintWriter Writer = new PrintWriter(file);			
    		Writer.printf("%d %d %d\n", 0, 1, 0);
    		for (int i = 1; i <= 1000; i++) {
    			if (i+1 <= 1000) {
    				Writer.printf("%d %d %.2f\n", i, i+1, (double) i);
    			}
    			if (2*i <= 1000) {
    				Writer.printf("%d %d %f\n", i, 2*i, convert(i));
    			}
    		}
    		Writer.close();
    		System.out.println("File is ready!");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * createGraphWithDijkstra:
     * Note: Since static methods are bad practice, we give the graph as the parameter.
     * 1. Reads a file containing edges (supplied by the user);
     * 2. Forms the graph;
     * 3. Runs the shortest path algorithm (Dijkstra)
     * 4. Checks for all the errors
     * The data file is a sequence of lines of the format
     *    source destination cost
     */
    
    public void createGraphWithDijkstra(Graph g) {
    	
    	//Asks to set a name to the input file.
    	System.out.println("Choose a new name for a file name or it will overwrite a previous one with the same name...");
    	System.out.print("Set name of the input file (e.g graph.dat): ");
    	Scanner scInput = new Scanner(System.in);
    	String inputFileName = scInput.nextLine();
    	g.exponentFileMaker(inputFileName);

    	try
    	{
    		//Asks to give the path of the input file.
    		System.out.print("Enter previously mentioned file's Path: ");
    		Scanner scPath = new Scanner(System.in);
    		String path = scPath.nextLine();
    		FileReader fin = new FileReader( path );
    		Scanner graphFile = new Scanner( fin );
    		
    		// Read the edges and insert
    		String line;
    		while( graphFile.hasNextLine( ) )
    		{

    			line = graphFile.nextLine( );
    			StringTokenizer st = new StringTokenizer( line );
    			try
    			{
    				//Checks if the input file is in the right format.
    				if( st.countTokens( ) != 3 )
    				{
    					System.err.println( "Skipping ill-formatted line " + line );
    					continue;
    				}
    				//Separates the 3 components to construct a graph.
    				String source  = st.nextToken( );
    				String dest    = st.nextToken( );
    				double    cost    = Double.parseDouble( st.nextToken( ) );
    				g.addEdge( source, dest, cost );
    			}
    			catch( NumberFormatException e )
    			{ System.err.println( "Skipping ill-formatted line " + line ); }
    		}       
    	}
    	catch( IOException e )
    	{ System.err.println( e ); }

    	//Prints out number of vertices read.
    	System.out.println( "File read..." );
    	System.out.println( g.vertexMap.size( ) + " vertices" );
    	
    	//Asks to set the name to the output file.
    	System.out.print("Set name of the output file (e.g graph-output.dat): ");
    	Scanner scOutput = new Scanner(System.in);
    	String outputFileName = scOutput.nextLine();

    	File file = new File(outputFileName);		
    	if (!file.exists()) {
    		try {
    			file.createNewFile();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	try {
    		//Writes to a file using PrintWriter.
    		PrintWriter wrt = new PrintWriter(file);
    		g.performDijkstra(g, wrt);
    		wrt.close();
    		System.out.println("Done! Check your folder for files!");
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * main: This creates an object of type Graph and executes createGraphWithDijkstra.
     */
    
    public static void main( String [ ] args )
    {
    	Graph G = new Graph();
    	G.createGraphWithDijkstra(G);
    }

}
/* Copyright © 2019 Izan Ahmed and Mark Weiss Allen*/
/* I used help from Shervin Hajiamini to understand the assignment and some may or may not be his implementation. 
 * Most of the Functions/Classes are from Mark Weiss Allen.
 * I only worked with Graph.java.
 * I only added a few functions, createGraphWithDijkstra (but most implementation was by Allen)
 * I also added exponentFileMaker and convert which were written by me.
 * I edited the processRequest to performDijkstra, which directly prints out to a file and converted back to a standard 
 * 		function instead of a static function.
 * I also edited the public printPath and private printPath to directly print to a file.
 * I also removed other shortest-path algorithms as they were unnecessary for this assignment.
 * If you want to see the original files please use Grinnell's MathLAN and enter: 
 * 					/home/hajiamini/courses/CSC207/code/wordladders
 */