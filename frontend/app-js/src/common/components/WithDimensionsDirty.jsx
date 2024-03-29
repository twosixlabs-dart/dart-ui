import React, {
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import PropTypes from 'prop-types';

function WithDimensionsDirty(props) {
  const {
    children,
    className,
    setHeight,
    setWidth,
    style,
  } = props;
  const outerRef = useRef(null);
  const [outerHeight, setOuterHeight] = useState(0);
  const [outerWidth, setOuterWidth] = useState(0);

  const getSizeAndSet = () => {
    if (outerRef !== null && outerRef.current) {
      const computedStyle = getComputedStyle(outerRef.current);
      const totalHeight = outerRef.current.clientHeight;
      const paddingHeight = parseFloat(computedStyle.paddingTop)
        + parseFloat(computedStyle.paddingBottom);
      const newHeight = totalHeight - paddingHeight;
      const totalWidth = outerRef.current.clientWidth;
      const paddingWidth = parseFloat(computedStyle.paddingLeft)
        + parseFloat(computedStyle.paddingRight);
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
    <div ref={outerRef} className={className} style={style}>
      {children({ outerHeight, outerWidth })}
    </div>
  );
}

WithDimensionsDirty.propTypes = {
  children: PropTypes.func.isRequired,
  className: PropTypes.string,
  setHeight: PropTypes.func,
  setWidth: PropTypes.func,
  style: PropTypes.shape({}),
};

WithDimensionsDirty.defaultProps = {
  className: null,
  setHeight: () => {},
  setWidth: () => {},
  style: {},
};

export default WithDimensionsDirty;
