package pl.cmil.wuff.plugin.diagnostic;

public class BundleDiagnosis
{
    private String name;
    private Status status;
    private int id;

    public BundleDiagnosis( String name, Status status, int id )
    {
        this.name = name;
        this.status = status;
        this.id = id;
    }

    public static BundleDiagnosis from( String[] data )
    {
        String[] nameAndStatus = data[ 1 ].split( "[ ]+" );

        return new BundleDiagnosis( nameAndStatus[ 1 ], Status.getFrom( nameAndStatus[ 0 ] ),
            Integer.parseInt( data[ 0 ] ) );
    }

    public String getName()
    {
        return name;
    }

    public Status getStatus()
    {
        return status;
    }

    public int getId()
    {
        return id;
    }

    public enum Status
    {
        OK,NOT;

        public static Status getFrom( String status )
        {
            if( status.equals( "INSTALLED" ) )
            {
                return NOT;
            }
            return OK;
        }

    }

    @Override
    public String toString()
    {
        return getId() + " " + getName() + " " + getStatus();
    }
}
