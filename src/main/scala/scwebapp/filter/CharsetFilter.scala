package scwebapp.filter

import javax.servlet._
import javax.servlet.http._

import scutil.ext.AnyRefImplicits._

/**sets request and response character encodings to the value of the init-parameter "charset" */
final class CharsetFilter extends Filter {
	@volatile private var filterConfig:Option[FilterConfig] 	= None
	
	def init(filterConfig:FilterConfig) {
		this.filterConfig	= Some(filterConfig)
	}
	
	def destroy() {
		this.filterConfig	= None
	}
	
	def doFilter(request:ServletRequest, response:ServletResponse, filterChain:FilterChain) {
		for {
			config	<- filterConfig
			charset	<- Option(config getInitParameter "charset")
		}
		yield {
			// config.getServletContext log ("filter: " + response.getContentType)
			request  setCharacterEncoding charset
			response setCharacterEncoding charset
			filterChain doFilter (request, response)
		}
	}
}
