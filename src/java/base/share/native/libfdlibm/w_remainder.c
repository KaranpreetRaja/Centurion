/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

/*
 * wrapper remainder(x,p)
 */

#include "fdlibm.h"

#ifdef __STDC__
        double remainder(double x, double y)    /* wrapper remainder */
#else
        double remainder(x,y)                   /* wrapper remainder */
        double x,y;
#endif
{
#ifdef _IEEE_LIBM
        return __ieee754_remainder(x,y);
#else
        double z;
        z = __ieee754_remainder(x,y);
        if(_LIB_VERSION == _IEEE_ || isnan(y)) return z;
        if(y==0.0)
            return __kernel_standard(x,y,28); /* remainder(x,0) */
        else
            return z;
#endif
}
