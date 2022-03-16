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
  } = props;
  const outerRef = useRef(null);
  const [outerHeight, setOuterHeight] = useState(0);
  const [outerWidth, setOuterWidth] = useState(0);

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
    <div ref={outerRef} className={className}>
      {children({ outerHeight, outerWidth })}
    </div>
  );
}

WithDimensionsDirty.propTypes = {
  children: PropTypes.func.isRequired,
  className: PropTypes.string,
  setHeight: PropTypes.func,
  setWidth: PropTypes.func,
};

WithDimensionsDirty.defaultProps = {
  className: null,
  setHeight: () => {},
  setWidth: () => {},
};

export default WithDimensionsDirty;
