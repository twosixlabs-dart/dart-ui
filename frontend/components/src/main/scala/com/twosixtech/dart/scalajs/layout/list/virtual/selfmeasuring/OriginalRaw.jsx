import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import debounce from 'lodash/debounce';

import { defaultProps, propTypes } from './SelfMeasuringWindowedListPropTypes';

class SelfMeasuringWindowedListRaw extends Component {
  constructor(props) {
    super(props);
    this.initializeRowHeights = this.initializeRowHeights.bind(this);
    this.debouncedInitRowHeights = debounce(this.initializeRowHeights, 300);
    this.bindRowRef = this.bindRowRef.bind(this);
    this.getWindowDimensions = this.getWindowDimensions.bind(this);
    this.scrollHandler = this.scrollHandler.bind(this);
    this.updateRow = this.updateRow.bind(this);
    this.measureAll = this.measureAll.bind(this);
    this.state = { scrollOffset: 0 };
    this.initializeRowHeights();
  }

  shouldComponentUpdate(newProps) {
    const {
      rowHeightsArray,
      oldScrollToIndex,
      oldScrollChange,
      oldScrollTop,
      debouncedInitRowHeights,
      props,
    } = this;
    const { rowCount, windowHeight, windowWidth } = newProps;
    const oldWindowWidth = props.windowWidth;
    const newScrollTop = newProps.scrollTop;
    const newScrollToIndex = newProps.scrollToIndex;
    const newScrollChange = newProps.scrollOffset;

    if (windowWidth !== oldWindowWidth) {
      debouncedInitRowHeights();
      return true;
    }

    if (rowHeightsArray[0].height !== undefined) {
      if (newScrollToIndex !== oldScrollToIndex
        && newScrollToIndex !== -1
        && newScrollToIndex < rowCount) {
        const minNewScrollOffset = newScrollToIndex === 0
          ? 0 : this.rowHeightsArray[newScrollToIndex - 1].totalSize;
        const correctedScrollOffset = minNewScrollOffset - (windowHeight / 2);
        const newScrollOffset = correctedScrollOffset < 0 ? 0 : correctedScrollOffset;
        this.setState(() => ({ scrollOffset: newScrollOffset }));
        this.moveToIndex = true;
      } else if (newScrollTop !== oldScrollTop && newScrollTop !== -1) {
        this.setState(() => ({ scrollOffset: newScrollTop }));
        this.moveToScrollTop = true;
      } else if (newScrollChange !== oldScrollChange && newScrollChange !== -1) {
        this.setState(({ scrollOffset }) => ({ scrollOffset: scrollOffset + newScrollChange }));
        this.moveToOffset = true;
      }
    }

    return true;
  }

  componentDidUpdate() {
    const { rowHeightsArray, windowRef, props } = this;
    const {
      rowCount,
      scrollToIndex,
      scrollToIndexCallback,
      scrollTop,
      scrollOffsetCallback,
    } = props;
    const { state: { scrollOffset } } = this;
    const scrollOffsetProp = props.scrollOffset;

    // Control scroll
    if (windowRef) {
      windowRef.scrollTop = scrollOffset;
      if (this.moveToIndex) {
        this.oldScrollToIndex = scrollToIndex;
        this.moveToIndex = false;
        scrollToIndexCallback();
      } else if (this.moveToScrollTop) {
        this.oldScrollTop = scrollTop;
        this.moveToScrollTop = false;
      } else if (this.moveToOffset) {
        this.oldScrollChange = scrollOffsetProp;
        this.moveToOffset = false;
        scrollOffsetCallback();
      }
    }

    // Measure displayed rows
    if (this.displayedRows) {
      const updatedRows = [];
      for (let i = this.displayedRows[0]; i <= this.displayedRows[1]; i += 1) {
        let newHeight = rowHeightsArray[i].height;
        if (rowHeightsArray[i].ref) {
          newHeight = this.rowHeightsArray[i].ref.clientHeight;
          this.rowHeightsArray[i].ref = null;
        }
        const newTotalSize = i === 0
          ? newHeight : rowHeightsArray[i - 1].totalSize + newHeight;
        const heightDiff = newHeight - rowHeightsArray[i].height;
        const totalSizeDiff = newTotalSize - rowHeightsArray[i].totalSize;
        if (heightDiff > 2 || totalSizeDiff > 2) {
          rowHeightsArray[i].height = newHeight;
          rowHeightsArray[i].totalSize = newTotalSize;
          rowHeightsArray[i].measured = true;
          updatedRows.push(i);
        }
      }
      if (updatedRows.length > 0) {
        updatedRows.sort((a, b) => a - b);
        if (updatedRows[0] === 0) {
          rowHeightsArray[0].totalSize = rowHeightsArray[0].height;
        } else {
          rowHeightsArray[updatedRows[0]].totalSize = rowHeightsArray[updatedRows[0] - 1].totalSize
            + rowHeightsArray[updatedRows[0]].height;
        }
        for (let i = updatedRows[0] + 1; i < rowCount; i += 1) {
          rowHeightsArray[i].totalSize = rowHeightsArray[i - 1].totalSize
            + rowHeightsArray[i].height;
        }
      }
    }
  }

  getWindowDimensions() {
    if (this.rowHeightsArray[0].totalSize !== undefined) {
      const {
        overscanCount,
        rowCount,
        windowHeight,
      } = this.props;

      const { scrollOffset } = this.state;

      let windowStartIndex = 0;
      while (windowStartIndex < rowCount - 1
      && this.rowHeightsArray[windowStartIndex].totalSize < scrollOffset) {
        windowStartIndex += 1;
      }
      const startIndex = windowStartIndex - overscanCount < 0
        ? 0 : windowStartIndex - overscanCount;
      let paddingBefore = 0;
      if (startIndex > 0) {
        paddingBefore = this.rowHeightsArray[startIndex - 1].totalSize;
      }
      let windowEndIndex = windowStartIndex;
      let runningSize = this.rowHeightsArray[windowEndIndex].height;
      while (runningSize < windowHeight && windowEndIndex < rowCount - 1) {
        windowEndIndex += 1;
        runningSize += this.rowHeightsArray[windowEndIndex].height;
      }
      const endIndex = windowEndIndex + overscanCount > rowCount - 1
        ? rowCount - 1 : windowEndIndex + overscanCount;
      const paddingAfter = this.rowHeightsArray[rowCount - 1].totalSize
        - this.rowHeightsArray[endIndex].totalSize;

      return {
        startIndex,
        endIndex,
        paddingBefore,
        paddingAfter,
      };
    }

    return {};
  }

  // eslint-disable-next-line class-methods-use-this
  updateRow() {
  }

  scrollHandler(e) {
    const { scrollTop } = e.target;
    const { props: { onScroll } } = this;
    onScroll(scrollTop);
    this.setState(() => ({ scrollOffset: scrollTop }));
  }

  initializeRowHeights() {
    const { rowCount } = this.props;
    this.rowHeightsArray = new Array(rowCount);
    this.displayedRows = [];
    this.moveToIndex = false;
    this.moveToOffset = false;
    this.moveToScrollTop = false;
    this.oldScrollChange = null;
    this.oldScrollToIndex = null;
    this.oldScrollTop = null;
    for (let i = 0; i < rowCount; i += 1) {
      if (i === 0) {
        this.rowHeightsArray[0] = {
          height: 50,
          totalSize: 50,
          measured: false,
        };
      } else {
        this.rowHeightsArray[i] = {
          height: 50,
          totalSize: 50 + this.rowHeightsArray[i - 1].totalSize,
          measured: false,
        };
      }
    }
    this.rowToMeasure = 0;
    this.measureAll();
  }

  measureAll() {
    if (!this.rowToMeasure) {
      this.rowToMeasure = 0;
    }
    const {
      props,
      rowToMeasure,
      rowHeightsArray,
    } = this;

    const {
      rowCount,
      overscanCount,
      rowForMeasure,
      rowForWindow,
      windowWidth,
    } = props;

    window.requestIdleCallback(() => {
      const rowRenderer = rowForMeasure || rowForWindow;

      const invisibleContainer = document.createElement('div');
      invisibleContainer.style.visibility = 'hidden';
      invisibleContainer.style.width = `${windowWidth}px`;
      document.body.append(invisibleContainer);

      let measureLimit = rowToMeasure + (2 * overscanCount);
      if (measureLimit > rowCount) {
        measureLimit = rowCount;
      }

      const allElements = rowHeightsArray
        .slice(rowToMeasure, measureLimit)
        .map((v, i) => {
          const row = i + rowToMeasure;

          return (
            <div
              id={`row-for-measurement-${row}`}
              key={`${Math.random()}-${Math.random()}`}
            >
              {rowRenderer({
                index: row,
                updateRow: this.updateRow,
                parent: this.windowRef,
              })}
            </div>
          );
        });

      const handleRenderedElements = () => {
        let min = null;
        let i;
        for (i = rowToMeasure; i < measureLimit; i += 1) {
          const iEle = document.getElementById(`row-for-measurement-${i}`);
          const newHeight = iEle.clientHeight;
          rowHeightsArray[i].height = newHeight;
          rowHeightsArray[i].measured = true;
          if (min === null) {
            min = i;
          }
        }

        // Clean up, now that we have all our heights;
        ReactDOM.unmountComponentAtNode(invisibleContainer);
        invisibleContainer.remove();

        if (min !== null) {
          for (let j = min; j < measureLimit; j += 1) {
            rowHeightsArray[j].totalSize = j === 0
              ? rowHeightsArray[j].height
              : rowHeightsArray[j].height + rowHeightsArray[j - 1].totalSize;
          }
        }
        if (i < rowCount - 1) {
          this.rowToMeasure = i;
          this.measureAll();
        } else {
          this.rowToMeasure = null;
        }
        this.forceUpdate();
      };

      // eslint-disable-next-line func-names,react/no-render-return-value
      ReactDOM.render(allElements, invisibleContainer, handleRenderedElements);
    });
  }

  bindRowRef(index) {
    return (ref) => {
      this.rowHeightsArray[index].ref = ref;
    };
  }

  render() {
    const {
      rowForWindow,
      windowWidth,
      windowHeight,
      key,
      rowCount,
    } = this.props;

    const windowStyle = {
      width: windowWidth,
      maxHeight: windowHeight,
      overflowY: 'auto',
      overflowX: 'none',
    };

    const dimensions = this.getWindowDimensions();
    const {
      startIndex,
      endIndex,
      paddingBefore,
    } = dimensions;

    this.displayedRows = [startIndex, endIndex];
    const rowHeightsSlice = this.rowHeightsArray.slice(startIndex, endIndex + 1);

    return (
      <div
        style={windowStyle}
        ref={(ref) => { this.windowRef = ref; }}
        onScroll={this.scrollHandler}
      >
        <div
          style={{ width: windowWidth, height: this.rowHeightsArray[rowCount - 1].totalSize }}
        >
          <div style={{ width: '100%', height: paddingBefore }} />
          {rowHeightsSlice.map((v, i0) => {
            const i = i0 + startIndex;
            const rowRefCallback = (ref) => {
              this.rowHeightsArray[i].ref = ref;
            };
            const style = { width: '100%' };
            return (
              <div
                style={style}
                ref={rowRefCallback}
                key={`window-${key}-${i}`}
              >
                {rowForWindow({
                  index: i,
                  updateRow: this.updateRow,
                  parent: this.windowRef,
                })}
              </div>
            );
          })}
        </div>
      </div>
    );
  }
}

SelfMeasuringWindowedListRaw.propTypes = propTypes;

SelfMeasuringWindowedListRaw.defaultProps = defaultProps;

export default SelfMeasuringWindowedListRaw;
