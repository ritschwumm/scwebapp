package scwebapp

trait HttpPart {
	def name:String
	def size:Long
	def headers:HttpHeaders
	def body:HttpInput
}
