/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

/*
 * wrapper log(x)
 */

#include "fdlibm.h"


#ifdef __STDC__
        double log(double x)            /* wrapper log */
#else
        double log(x)                   /* wrapper log */
        double x;
#endif
{
#ifdef _IEEE_LIBM
        return __ieee754_log(x);
#else
        double z;
        z = __ieee754_log(x);
        if(_LIB_VERSION == _IEEE_ || isnan(x) || x > 0.0) return z;
        if(x==0.0)
            return __kernel_standard(x,x,16); /* log(0) */
        else
            return __kernel_standard(x,x,17); /* log(x<0) */
#endif
}
