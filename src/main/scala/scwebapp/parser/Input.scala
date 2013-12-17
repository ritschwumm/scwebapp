package scwebapp.parser

trait Input[+S] {
	def next:Option[(Input[S],S)]
	// def max(that:Input[S]):Input[S] =
}
