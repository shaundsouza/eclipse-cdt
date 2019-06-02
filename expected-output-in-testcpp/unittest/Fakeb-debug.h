#ifndef _FAKE_null
#define _FAKE_null

#include "src\b.h"


void printFakeFunction();

#include "Fake.h"
#include "FakeMethodVerifiers.h"

namespace eGlobal <unnamed scope> (org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope@350c7188) {

class Fakeb : public Fake, public b
{
public:
  explicit Fakeb();
  virtual ~Fakeb();

  virtual void registerMethodWatcher( TestUtility::FakeMethodWatcher& methodWatcher );
  virtual void verifyFakeMethodWasNotCalled( const std::string& testCondition );
  virtual void verifyFakeMethodUsage( const std::string& testCondition );
  virtual void resetFakeMethodUsage();

  virtual int d(int a, int b);
  virtual void c(int a);

  mutable TestUtility::FakeMethod< int( int, int ) > fake_d;
  mutable TestUtility::FakeMethod< void( int ) > fake_c;

};

} // namespace eGlobal <unnamed scope> (org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespaceScope@350c7188)

#endif // _FAKE_null
