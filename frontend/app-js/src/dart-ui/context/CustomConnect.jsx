import React from 'react';
import { connect as reduxConnect } from 'react-redux';
import { ctx } from './contextProvider';

// eslint-disable-next-line import/prefer-default-export
export function connect(mapStateToProps, mapDispatchToProps) {
  // Converts dart's mapStateToProps function into a redux mapStateToProps function
  // by expecting to receive dartContext as a prop in the redux-connected component
  // eslint-disable-next-line max-len
  const reduxMapStateToProps = (state, props) => mapStateToProps(state, props.dartContextInjectionSite);

  return (WrappedComponent) => {
    // Create a proxy of WrappedComponent that removes the dartContextInjectionSite
    // prop, which is only there for consumption by reduxConnect
    const ProxyComponent = (props) => {
      const filteredProps = { ...props };
      delete filteredProps.dartContextInjectionSite;

      // eslint-disable-next-line react/prop-types
      const { children } = props;
      return (<WrappedComponent {...filteredProps}>{children}</WrappedComponent>);
    };

    const ReduxComponent = reduxConnect(reduxMapStateToProps, mapDispatchToProps)(ProxyComponent);

    // Note dartContext is passed to the ReduxComponent as prop dartInjectionSite
    return (props) => (
      <ctx.Consumer>
        {(dartContext) => (
          <ReduxComponent
            {...props}
            dartContextInjectionSite={dartContext}
          >
            {/* eslint-disable-next-line react/destructuring-assignment,react/prop-types */}
            {props.children}
          </ReduxComponent>
        )}
      </ctx.Consumer>
    );
  };
}
