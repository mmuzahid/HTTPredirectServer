/**interface to implement for a Location Resolver*/
public interface LocationResolver<T> {
	/**returns location string for HTTP header <strong>Location</strong>*/
	String getLocation(T t);
}
