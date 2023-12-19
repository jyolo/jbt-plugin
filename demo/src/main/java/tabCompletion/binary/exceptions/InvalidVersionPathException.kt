package tabCompletion.binary.exceptions

class InvalidVersionPathException(version: String) : RuntimeException("Given version path is invalid: '$version'")
