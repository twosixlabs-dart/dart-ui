import React, {
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core';
import WithDimensionsProps from './WithDimensionsProps';

const styles = () => ({
  root: {
    height: '100%',
    width: '100%',
  },
});

function WithDimensionsRaw(props) {
  const {
    render,
    classes,
    setHeight,
    setWidth,
  } = props;
  const outerRef = useRef(null);
  const [outerHeight, setOuterHeight] = useState(null);
  const [outerWidth, setOuterWidth] = useState(null);

  const getSizeAndSet = () => {
    if (outerRef !== null && outerRef.current) {
      const style = getComputedStyle(outerRef.current);
      const totalHeight = outerRef.current.clientHeight;
      const paddingHeight = parseFloat(style.paddingTop) + parseFloat(style.paddingBottom);
      const newHeight = totalHeight - paddingHeight;
      const totalWidth = outerRef.current.clientWidth;
      const paddingWidth = parseFloat(style.paddingLeft) + parseFloat(style.paddingRight);
      const newWidth = totalWidth - paddingWidth;
      if (newHeight !== outerHeight) {
        setOuterHeight(newHeight);
        setHeight(newHeight);
      }
      if (newWidth !== outerWidth) {
        setOuterWidth(newWidth);
        setWidth(newWidth);
      }
    }
  };

  const handleResize = useCallback(() => {
    getSizeAndSet();
  }, []);

  useEffect(() => {
    getSizeAndSet();
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [handleResize]);

  useEffect(() => {
    getSizeAndSet();
  }, []);

  return (
    <div ref={outerRef} className={classes.root}>
      {render({ outerHeight, outerWidth })}
    </div>
  );
}

WithDimensionsRaw.propTypes = {
  ...WithDimensionsProps.propTypes,
  classes: PropTypes.shape({
    root: PropTypes.string,
  }).isRequired,
};

WithDimensionsRaw.defaultProps = WithDimensionsProps.defaultProps;

export default withStyles(styles)(WithDimensionsRaw);
