object Main {

	def main( args : Array[ String ] ) : Unit = {
		val argsList = args.toList
		argsList match {
			case Nil =>
				println( "Specify utility: translate-back, clean-removals" )
				System.exit( 1 )
			case cmd :: otherArgs =>
				cmd match {
					case "translate-back" =>
						TranslateOntology.main( otherArgs.toArray )

					case "clean-removals" =>
						IdentifyAccidentalExampleRemovals.main( otherArgs.toArray )

					case unknownCommand =>
						println( s"Unknown command: $unknownCommand" )
						System.exit( 2 )
				}
		}
	}

}
