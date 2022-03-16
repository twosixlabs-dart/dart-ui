import React, { Suspense } from 'react';
import PropTypes from 'prop-types';

const fallback = (<div>Loading...</div>);

export default function LazyElement(props) {
  const { children } = props;

  return (
    <Suspense fallback={fallback}>
      {children}
    </Suspense>
  );
}

LazyElement.propTypes = {
  children: PropTypes.element.isRequired,
};
