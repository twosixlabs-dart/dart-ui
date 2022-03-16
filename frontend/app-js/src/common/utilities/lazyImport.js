import React from 'react';

export default function reactLazy(importPromise) {
  return React.lazy(() => importPromise);
}
