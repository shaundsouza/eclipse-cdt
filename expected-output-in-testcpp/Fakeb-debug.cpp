#include "Fakeb.h"

void printFakeFunction();

namespace FakebNamespace {
} // namespace FakebNamespace

namespace eGlobal <unnamed scope> (org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope@5ac30b75) {


Fakeb::Fakeb()
: b(  )
, fake_d( "Fakeb::d" )
, fake_c( "Fakeb::c" )
{}

Fakeb::~Fakeb()
{}

void Fakeb::registerMethodWatcher( TestUtility::FakeMethodWatcher& methodWatcher )
{
  fake_d.registerMethodWatcher( methodWatcher );
  fake_c.registerMethodWatcher( methodWatcher );
}

void Fakeb::verifyFakeMethodWasNotCalled( const std::string& testCondition )
{
  TestUtility::verifyFakeMethodWasNotCalled( fake_d, testCondition );
  TestUtility::verifyFakeMethodWasNotCalled( fake_c, testCondition );
}

void Fakeb::verifyFakeMethodUsage( const std::string& testCondition )
{
  TestUtility::verifyFakeMethodUsage( fake_d, testCondition );
  TestUtility::verifyFakeMethodUsage( fake_c, testCondition );
}

void Fakeb::resetFakeMethodUsage( void )
{
  fake_d.reset();
  fake_c.reset();
}

int Faked(int a, int b)
{
  return fake_d( a, b );
}

void Fakec(int a)
{
  return fake_c( a );
}

} // namespace eGlobal <unnamed scope> (org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope@5ac30b75)
