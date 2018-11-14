package scwebapp.servlet

import javax.servlet._

/** sets request and response character encodings to the value of the init-parameter "charset" */
final class CharsetFilter extends Filter {
	@volatile private var filterConfig:Option[FilterConfig] 	= None

	def init(filterConfig:FilterConfig) {
		this.filterConfig	= Some(filterConfig)
	}

	def destroy() {
		this.filterConfig	= None
	}

	private def charset:String	=
			configCharset getOrElse "UTF-8"

	private def configCharset:Option[String]	=
			for {
				config	<- filterConfig
				charset	<- Option(config getInitParameter "charset")
			}
			yield charset

	//------------------------------------------------------------------------------

	def doFilter(request:ServletRequest, response:ServletResponse, filterChain:FilterChain) {
		// config.getServletContext log ("filter: " + response.getContentType)
		request  setCharacterEncoding charset
		response setCharacterEncoding charset
		filterChain doFilter (request, response)
	}
}