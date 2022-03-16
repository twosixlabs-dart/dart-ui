// import React from 'react';
//
// import Paper from '@material-ui/core/Paper';
// import makeStyles from '@material-ui/core/styles/makeStyles';
// import Typography from '@material-ui/core/Typography';
// import Grid from '@material-ui/core/Grid';
// import DartHeaderProps from './DartHeaderProps';
//
// const useStyles = makeStyles(() => ({
//   paper: {
//     height: 48,
//   },
//   smallPaper: {
//     padding: 3,
//   },
//   container: {
//     height: '100%',
//   },
//   root: {},
// }));
//
// const makeTitle = (title) => (
//   <Typography variant="subtitle1" component="h3" color="primary">
//     <b>{title}</b>
//   </Typography>
// );
//
// export default function DartHeader(props) {
//   const { element, title, small } = props;
//   const classes = useStyles();
//
//   const paperClass = small ? classes.smallPaper : classes.paper;
//   const rootClass = classes.root || '';
//
//   return (
//     <Paper square className={`${paperClass} ${rootClass}`}>
//       <Grid container direction="column" justify="center" alignItems="center" classes={{ root: classes.container }}>
//         <Grid container direction="row" justify="center" alignItems="center" classes={{ root: classes.container }}>
//           {title ? makeTitle(title) : element}
//         </Grid>
//       </Grid>
//     </Paper>
//   );
// }
//
// DartHeader.propTypes = DartHeaderProps.propTypes;
// DartHeader.defaultProps = DartHeaderProps.defaultProps;
