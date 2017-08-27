public class Resource{

	//capacity: The total number of units the resource can hold. available: Number of units currently available. 
	public int capacity;
	public int available;

	//Default constructor.
	public Resource(int capacity){
		this.capacity = capacity;
		available = capacity;
	}

	//Copy constructor.
	public Resource(Resource r){
		capacity = r.capacity;
		available = r.available;
	}

}