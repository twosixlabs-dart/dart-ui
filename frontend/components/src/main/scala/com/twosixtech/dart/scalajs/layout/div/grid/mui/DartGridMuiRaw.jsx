// import React, { Component } from 'react';
// // import ReactDOM from 'react-dom';
// import { Grid, withStyles } from '@material-ui/core';
//
// import DartGridProps from './DartGridMuiProps';
//
// const styles = () => ({
//   root: {
//     height: '100%',
//   },
//   container: {},
//   items: {},
// });
//
// class DartGridRaw extends Component {
//   render() {
//     const {
//       direction,
//       align,
//       items,
//       classes,
//     } = this.props;
//
//     let alignItems = 'flex-start';
//     switch (align) {
//       case 'start': {
//         alignItems = 'flex-start';
//         break;
//       }
//
//       case 'center': {
//         alignItems = 'center';
//         break;
//       }
//
//       case 'end': {
//         alignItems = 'flex-end';
//         break;
//       }
//
//       default:
//         break;
//     }
//
//     return (
//       <Grid
//         container
//         direction={direction}
//         alignItems={alignItems}
//         classes={{ root: classes.container }}
//       >
//         {items.map((item, i) => {
//           const {
//             breakPoints,
//             element,
//             key,
//           } = item;
//           return (
//             <Grid
//               item
//               xs={breakPoints.xs}
//               sm={breakPoints.sm}
//               md={breakPoints.md}
//               lg={breakPoints.lg}
//               xl={breakPoints.xl}
//               key={key || `grid-item-${i}`}
//               classes={{ root: classes.items }}
//             >
//               {element}
//             </Grid>
//           );
//         })}
//       </Grid>
//     );
//   }
// }
//
// DartGridRaw.propTypes = DartGridProps.propTypes;
// DartGridRaw.defaultProps = DartGridProps.defaultProps;
//
// export default withStyles(styles)(DartGridRaw);
