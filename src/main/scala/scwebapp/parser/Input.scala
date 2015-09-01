package scwebapp.parser

trait Input[+S] {
	def next:Option[(Input[S],S)]
}
