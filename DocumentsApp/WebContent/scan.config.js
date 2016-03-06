//
// Dynamsoft JavaScript Library for Basic Initiation of Dynamic Web TWAIN
// More info on DWT: http://www.dynamsoft.com/Products/WebTWAIN_Overview.aspx
//
// Copyright 2015, Dynamsoft Corporation 
// Author: Dynamsoft Team
// Version: 11.1
//
/// <reference path="dynamsoft.webtwain.initiate.js" />
var Dynamsoft = Dynamsoft || { WebTwainEnv: {} };

Dynamsoft.WebTwainEnv.AutoLoad = true;
///
Dynamsoft.WebTwainEnv.Containers = [{ContainerId:'dwtcontrolContainer', Width:270, Height:350}];
///
Dynamsoft.WebTwainEnv.ProductKey = '0B18A11DE638B6F85E7AEE9375222C472A4DEF47C2D0E8112EF3B2AC71AD37592A4DEF47C2D0E811184EF1C2DF0268EE2A4DEF47C2D0E81152FD99A22C0A40802A4DEF47C2D0E8119CAA27EAE40C17A82A4DEF47C2D0E811342BDFF55B0FB3B52A4DEF47C2D0E811D2AF5EE56920BB832A4DEF47C2D0E8116442B1B0FCD5145A2A4DEF47C2D0E811ED295AAEC211FDBB80000000';
///
Dynamsoft.WebTwainEnv.Trial = true;
///
Dynamsoft.WebTwainEnv.ActiveXInstallWithCAB = false;
///
Dynamsoft.WebTwainEnv.Debug = false; // only for debugger output
///
Dynamsoft.WebTwainEnv.ResourcesPath = 'http://www.rembli.com/scanner';

/// All callbacks are defined in the dynamsoft.webtwain.install.js file, you can customize them.

// Dynamsoft.WebTwainEnv.RegisterEvent('OnWebTwainReady', function(){
// 		// webtwain has been inited
// });

