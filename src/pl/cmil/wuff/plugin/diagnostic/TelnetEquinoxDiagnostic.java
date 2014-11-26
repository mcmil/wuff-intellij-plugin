package pl.cmil.wuff.plugin.diagnostic;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TelnetEquinoxDiagnostic
{

    private List< String > diag;
    private List< String > ss;

    public boolean diagnose()
    {
        TelnetEquinoxDiagnosticDataSource ds = new TelnetEquinoxDiagnosticDataSource();

        List< List< String >> commands = ds.getCommands();

        if( commands.size() != 2 )
        {
            return false;
        }

        ss = commands.get( 0 );
        diag = commands.get( 1 );

        return true;
    }

    public String getDiag()
    {
        return String.join( "\n", diag );
    }

    public List< BundleDiagnosis > getBundles()
    {
        return ss.stream().filter( e -> e.matches( "^[0-9]+.*" ) ).map( e -> e.split( "\t" ) )
            .map( BundleDiagnosis::from ).sorted( getBundleComparator().reversed() )
            .collect( Collectors.toList() );

    }

    private Comparator< BundleDiagnosis > getBundleComparator()
    {
        return ( BundleDiagnosis x, BundleDiagnosis y ) -> {
            int statusCompare = x.getStatus().compareTo( y.getStatus() );
            if( statusCompare != 0 )
            {
                return statusCompare;
            }
            return Integer.compare( x.getId(), y.getId() );
        };
    }
}
