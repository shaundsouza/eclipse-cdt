#include "framework/FakeEventList.h"

#include <tut.h>

using namespace eGlobal <unnamed scope> (org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope@5ac30b75);

namespace tut {

struct b_data
{
  ~b_data()
  {
  }

  void verifyFakeMethodUsage( const std::string& testCondition )
  {
  }

};

typedef test_group<b_data> tg;
typedef tg::object object;
tg b_group( "eGlobal <unnamed scope> (org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope@5ac30b75)::b" );

// =================================================
// Constructor
// =================================================

/**
 * Test that the constructor works properly when passed valid arguments.
 */
template<>
template<>
void object::test<1>()
{
  b b(  );
  ensure( "the constructor works properly when passed valid arguments", true );
}

/**
 * Test that the  passed to the constructor
 * is returned by the getter.
 */
template<>
template<>
void object::test<2>()
{
  b b(  );

}

} // namespace tut
