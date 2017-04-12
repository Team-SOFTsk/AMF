# auto-common

Local copy of google's auto-common with fixed (quickfix) declared types equality check

MoreTypes$EqualVisitor.visitDeclared#189
when aElement and bElement are different references pointing to the same class, equality will
incorrectly fails