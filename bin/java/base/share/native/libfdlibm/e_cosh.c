/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

#include "fdlibm.h"

#ifdef __STDC__
static const double one = 1.0, half=0.5, huge = 1.0e300;
#else
static double one = 1.0, half=0.5, huge = 1.0e300;
#endif

#ifdef __STDC__
        double __ieee754_cosh(double x)
#else
        double __ieee754_cosh(x)
        double x;
#endif
{
        double t,w;
        int ix;
        unsigned lx;

    /* High word of |x|. */
        ix = __HI(x);
        ix &= 0x7fffffff;

    /* x is INF or NaN */
        if(ix>=0x7ff00000) return x*x;

    /* |x| in [0,0.5*ln2], return 1+expm1(|x|)^2/(2*exp(|x|)) */
        if(ix<0x3fd62e43) {
            t = expm1(fabs(x));
            w = one+t;
            if (ix<0x3c800000) return w;        /* cosh(tiny) = 1 */
            return one+(t*t)/(w+w);
        }

    /* |x| in [0.5*ln2,22], return (exp(|x|)+1/exp(|x|)/2; */
        if (ix < 0x40360000) {
                t = __ieee754_exp(fabs(x));
                return half*t+half/t;
        }

    /* |x| in [22, log(maxdouble)] return half*exp(|x|) */
        if (ix < 0x40862E42)  return half*__ieee754_exp(fabs(x));

    /* |x| in [log(maxdouble), overflowthresold] */
        lx = *( (((*(unsigned*)&one)>>29)) + (unsigned*)&x);
        if (ix<0x408633CE ||
              ((ix==0x408633ce)&&(lx<=(unsigned)0x8fb9f87d))) {
            w = __ieee754_exp(half*fabs(x));
            t = half*w;
            return t*w;
        }

    /* |x| > overflowthresold, cosh(x) overflow */
        return huge*huge;
}
