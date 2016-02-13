package scwebapp.header

import org.specs2.mutable._

import scwebapp.data._

class AcceptEncodingTest extends Specification {
	"AcceptEncoding" should {
		"parse empty" in {
			AcceptEncoding parse "" mustEqual
			Some(AcceptEncoding(Vector.empty))
		}
		"parse any" in {
			AcceptEncoding parse "*" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingWildcard,None))))
		}
		"parse any with quality" in {
			AcceptEncoding parse "*;q=1" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingWildcard,Some(QValue.one)))))
		}
		"parse identity" in {
			AcceptEncoding parse "identity" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingFixed(AcceptEncodingIdentity),None))))
		}
		"parse gzip" in {
			AcceptEncoding parse "gzip" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingFixed(AcceptEncodingOther(ContentEncodingGzip)),None))))
		}
		
		"accept identity when empty" in {
			AcceptEncoding parse "" map { _ acceptance AcceptEncodingIdentity } mustEqual
			Some(QValue(1000))
		}
		"not accept gzip when empty" in {
			AcceptEncoding parse "" map { _ acceptance AcceptEncodingOther(ContentEncodingGzip) } mustEqual
			Some(QValue(0))
		}
		"accept gzip with star" in {
			AcceptEncoding parse "*;q=0.5" map { _ acceptance AcceptEncodingOther(ContentEncodingGzip) } mustEqual
			Some(QValue(500))
		}
		"accept gzip with quality" in {
			AcceptEncoding parse "gzip;q=0.5" map { _ acceptance AcceptEncodingOther(ContentEncodingGzip) } mustEqual
			Some(QValue(500))
		}
		"override specific with wildcard" in {
			AcceptEncoding parse "*;q=0.7,gzip;q=0.5" map { _ acceptance AcceptEncodingOther(ContentEncodingGzip) } mustEqual
			Some(QValue(500))
		}
	}
}
