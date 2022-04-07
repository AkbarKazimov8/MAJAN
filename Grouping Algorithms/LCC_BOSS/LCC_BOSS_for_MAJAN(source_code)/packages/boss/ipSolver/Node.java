package boss.ipSolver;
import boss.general.IntegerPartition;
public class Node
{
	public Subspace subspace;
	public IntegerPartition integerPartition;
	public Edge[] edgesFromThisNode;
	boolean tempFlag; 
	public int[] tempIntegerRoots; 
	public Node( Subspace subspace ){
		this.subspace = subspace;
		integerPartition = new IntegerPartition( subspace.integers );
		edgesFromThisNode = null;
	}
}