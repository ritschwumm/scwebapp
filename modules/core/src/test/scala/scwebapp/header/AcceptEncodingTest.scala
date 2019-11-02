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
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Wildcard,None))))
		}
		"parse any with quality" in {
			AcceptEncoding parse "*;q=1" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Wildcard,Some(QValue.one)))))
		}
		"parse identity" in {
			AcceptEncoding parse "identity" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Fixed(AcceptEncodingType.Identity),None))))
		}
		"parse gzip" in {
			AcceptEncoding parse "gzip" mustEqual
			Some(AcceptEncoding(Vector(AcceptEncodingMatch(AcceptEncodingPattern.Fixed(AcceptEncodingType.Other(ContentEncodingType.Gzip)),None))))
		}

		"accept identity when empty" in {
			AcceptEncoding parse "" map { _ acceptance AcceptEncodingType.Identity } mustEqual
			Some(QValue(1000))
		}
		"not accept gzip when empty" in {
			AcceptEncoding parse "" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) } mustEqual
			Some(QValue(0))
		}
		"accept gzip with star" in {
			AcceptEncoding parse "*;q=0.5" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) } mustEqual
			Some(QValue(500))
		}
		"accept gzip with quality" in {
			AcceptEncoding parse "gzip;q=0.5" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) } mustEqual
			Some(QValue(500))
		}
		"override specific with wildcard" in {
			AcceptEncoding parse "*;q=0.7,gzip;q=0.5" map { _ acceptance AcceptEncodingType.Other(ContentEncodingType.Gzip) } mustEqual
			Some(QValue(500))
		}
	}
}
